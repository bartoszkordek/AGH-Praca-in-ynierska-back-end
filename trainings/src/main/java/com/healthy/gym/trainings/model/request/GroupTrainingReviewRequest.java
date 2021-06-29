package com.healthy.gym.trainings.model.request;


import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupTrainingReviewRequest {

    private String trainingTypeId;
    private int stars;
    private String text;

    public GroupTrainingReviewRequest(@JsonProperty("trainingTypeId") String trainingTypeId,
                                      @JsonProperty("stars") int stars,
                                      @JsonProperty("text") String text){
        this.trainingTypeId = trainingTypeId;
        this.stars = stars;
        this.text = text;
    }

    public String geTrainingTypeId() {
        return trainingTypeId;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }
}
