package com.healthy.gym.trainings.db;

import com.mongodb.client.*;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupTrainingsRepository {

    private static MongoClient mongoClient;
    private static MongoDatabase mdb;

    public static List<String> getGroupTrainings(){
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase("Gym");
        MongoCollection collection = mdb.getCollection("GroupTrainings");

        List<String> groupTrainingsList = new ArrayList<>();
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while(cursor.hasNext() && cursor!= null) {
                String trainingName = cursor.next().get("training_name").toString();
                groupTrainingsList.add(trainingName);
            }
        } finally {
            cursor.close();
        }
        return groupTrainingsList;
    }
}
