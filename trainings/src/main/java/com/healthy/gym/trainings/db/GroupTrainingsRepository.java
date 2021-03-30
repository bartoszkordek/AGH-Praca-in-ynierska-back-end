package com.healthy.gym.trainings.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.mongodb.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.util.ArrayList;
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
                        groupTrainingFromDb.getLimit()
                ));
            }
        } finally {
            cursor.close();
        }
        return groupTrainingsList;
    }
}
