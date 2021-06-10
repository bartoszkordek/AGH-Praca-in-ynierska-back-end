package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class GroupTrainingReviewResponse {

    @NotNull
    @JsonProperty("reviewId")
    private final String reviewId;
    @NotNull
    @JsonProperty("trainingName")
    private final String trainingName;
    @NotNull
    @JsonProperty("clientId")
    private final String clientId;
    @NotNull
    @JsonProperty("date")
    private final String date;
    @NotNull
    @JsonProperty("stars")
    private final int stars;
    @NotNull
    @JsonProperty("text")
    private final String text;

    public GroupTrainingReviewResponse(@NotNull @JsonProperty("reviewId") final String reviewId,
                                       @NotNull @JsonProperty("trainingName") final String trainingName,
                                       @NotNull @JsonProperty("clientId") final String clientId,
                                       @NotNull @JsonProperty("date") final String date,
                                       @NotNull @JsonProperty("stars") final int stars,
                                       @NotNull @JsonProperty("text") final String text){
        this.reviewId = reviewId;
        this.trainingName = trainingName;
        this.clientId = clientId;
        this.date = date;
        this.stars = stars;
        this.text = text;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDate() {
        return date;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }

}
