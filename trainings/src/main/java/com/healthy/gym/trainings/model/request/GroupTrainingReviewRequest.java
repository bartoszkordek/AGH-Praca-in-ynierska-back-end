package com.healthy.gym.trainings.model.request;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "GroupTrainingReviewRequest{" +
                "trainingTypeId='" + trainingTypeId + '\'' +
                ", stars=" + stars +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingReviewRequest that = (GroupTrainingReviewRequest) o;
        return stars == that.stars &&
                Objects.equals(trainingTypeId, that.trainingTypeId) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingTypeId, stars, text);
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
