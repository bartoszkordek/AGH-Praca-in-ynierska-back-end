package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingTypeModel {

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("description")
    private String description;

    public TrainingTypeModel(
            @JsonProperty("trainingName") String trainingName,
            @JsonProperty("description") String description){

        this.trainingName = trainingName;
        this.description = description;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public String getDescription() {
        return description;
    }
}
