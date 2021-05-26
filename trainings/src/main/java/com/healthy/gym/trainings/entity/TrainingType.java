package com.healthy.gym.trainings.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "TrainingType")
public class TrainingType {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatar")
    private byte[] avatar;

    public TrainingType(){

    }

    public TrainingType(String trainingName, String description, byte[] avatar){
        this.trainingName = trainingName;
        this.description = description;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
