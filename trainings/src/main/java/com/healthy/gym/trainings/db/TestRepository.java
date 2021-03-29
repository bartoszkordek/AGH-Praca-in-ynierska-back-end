package com.healthy.gym.trainings.db;

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository {

    private static MongoClient mongoClient;
    private static MongoDatabase mdb;

    public String getFirstTestDocument(){
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase("Test_Database");
        MongoCollection collection = mdb.getCollection("Test_Collection");

        FindIterable response = collection.find();

        return response.first().toString();
    }


    public static void main(String[] args) {
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase("Test_Database");
        MongoCollection collection = mdb.getCollection("Test_Collection");

        Document sampleDocument = new Document("_id", new ObjectId());
        sampleDocument.append("sampleKey1", "sampleValue1")
                .append("sampleKey2", "sampleValue2");

        collection.insertOne(sampleDocument);
    }


}
