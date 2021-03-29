package com.healthy.gym.trainings.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;

public class TestRepository {

    private static MongoClient mongoClient;
    private static MongoDatabase mdb;


    public static void main(String[] args) throws UnknownHostException {
        mongoClient = MongoClients.create();
        mdb = mongoClient.getDatabase("Test_Database");
        MongoCollection collection = mdb.getCollection("Test_Collection");

        Document sampleDocument = new Document("_id", new ObjectId());
        sampleDocument.append("sampleKey1", "sampleValue1")
                .append("sampleKey2", "sampleValue2");

        collection.insertOne(sampleDocument);

    }

}
