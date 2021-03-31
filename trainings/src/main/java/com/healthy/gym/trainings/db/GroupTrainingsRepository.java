package com.healthy.gym.trainings.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.mongodb.client.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class GroupTrainingsRepository {

    @Autowired
    private Environment environment;

    private MongoClient mongoClient;
    private MongoDatabase mdb;

    public List<GroupTrainingModel> getGroupTrainings() throws JsonProcessingException {
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase(environment.getProperty("microservice.db.name"));
        MongoCollection collection = mdb.getCollection(environment.getProperty("microservice.db.collection"));

        List<GroupTrainingModel> groupTrainingsList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while(cursor.hasNext() && cursor!= null) {
                GroupTrainingModel groupTrainingFromDb = objectMapper.readValue(cursor.next().toJson(), GroupTrainingModel.class);
                groupTrainingsList.add(new GroupTrainingModel(
                        groupTrainingFromDb.getId(),
                        groupTrainingFromDb.getTrainingName(),
                        groupTrainingFromDb.getTrainerId(),
                        groupTrainingFromDb.getDate(),
                        groupTrainingFromDb.getStartTime(),
                        groupTrainingFromDb.getEndTime(),
                        groupTrainingFromDb.getHallNo(),
                        groupTrainingFromDb.getLimit(),
                        groupTrainingFromDb.getParticipants()
                ));
            }
        } finally {
            cursor.close();
        }
        return groupTrainingsList;
    }

    public boolean isAbilityToGroupTrainingEnrollment(String trainingId){

        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase(environment.getProperty("microservice.db.name"));
        MongoCollection collection = mdb.getCollection(environment.getProperty("microservice.db.collection"));

        ObjectId oTrainingId = new ObjectId(trainingId.toString());
        Document idDoc = new Document("_id", oTrainingId);

        if(collection.find(idDoc).cursor().hasNext() == false) return false;

        MongoCursor<Document> cursor = collection.find(idDoc).iterator();
        List<String> participants = new ArrayList<>();
        try {
            boolean stop = false;
            while(cursor.hasNext() && cursor!= null && stop == false) {
                participants = (List<String>) cursor.next().get("participants");
                stop = true;
            }
        } finally {
            cursor.close();
        }
        Document gtParticipantsLimit = new Document("$gt", participants.size());
        Document limit = new Document("limit", gtParticipantsLimit);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);

        Document gtToday = new Document("$gt", todayDateFormatted);
        Document gtTodayDate = new Document("date", gtToday);

        Document match = new Document("$match", new Document("$and", Arrays.asList(idDoc,gtTodayDate,limit)));
        List<Document> pipeline = Arrays.asList(match);
        return collection.aggregate(pipeline).cursor().hasNext();
    }
}
