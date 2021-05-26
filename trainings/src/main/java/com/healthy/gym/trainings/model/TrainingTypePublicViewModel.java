package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingTypePublicViewModel {

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("avatar")
    private byte[] avatar;

    public TrainingTypePublicViewModel(
            @JsonProperty("trainingName") String trainingName,
            @JsonProperty("description") String description,
            @JsonProperty("avatar") byte[] avatar){

        this.trainingName = trainingName;
        this.description = description;
        this.avatar = avatar;
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
}
