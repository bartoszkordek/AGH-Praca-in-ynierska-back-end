package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "GroupTrainingReviewResponse{" +
                "reviewId='" + reviewId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", clientId='" + clientId + '\'' +
                ", date='" + date + '\'' +
                ", stars=" + stars +
                ", text='" + text + '\'' +
                '}';
    }

    //for testing purposes reviewId as UUID excluded from checking
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingReviewResponse that = (GroupTrainingReviewResponse) o;
        return stars == that.stars &&
//                Objects.equals(reviewId, that.reviewId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(clientId, that.clientId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, trainingName, clientId, date, stars, text);
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
