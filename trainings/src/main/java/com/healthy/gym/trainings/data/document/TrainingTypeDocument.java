package com.healthy.gym.trainings.data.document;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;

@Document(collection = "trainingType")
public class TrainingTypeDocument {

    @Id
    private String id;
    private String name;
    private String description;
    private Duration duration;
    private Binary image;
    private byte[] avatar;

    public TrainingTypeDocument() {

    }

    public TrainingTypeDocument(String trainingName, String description, byte[] avatar) {
        this.name = trainingName;
        this.description = description;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
