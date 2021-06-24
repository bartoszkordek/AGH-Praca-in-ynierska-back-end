package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class GroupTrainingReviewPublicResponse {

    @NotNull
    @JsonProperty("reviewId")
    private final String reviewId;
    @NotNull
    @JsonProperty("trainingName")
    private final String trainingName;
    @NotNull
    @JsonProperty("date")
    private final String date;
    @NotNull
    @JsonProperty("stars")
    private final int stars;
    @NotNull
    @JsonProperty("text")
    private final String text;

    public GroupTrainingReviewPublicResponse(@NotNull @JsonProperty("reviewId") final String reviewId,
                                             @NotNull @JsonProperty("trainingName") final String trainingName,
                                             @NotNull @JsonProperty("date") final String date,
                                             @NotNull @JsonProperty("stars") final int stars,
                                             @NotNull @JsonProperty("text") final String text) {
        this.reviewId = reviewId;
        this.trainingName = trainingName;
        this.date = date;
        this.stars = stars;
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupTrainingReviewPublicResponse{" +
                "reviewId='" + reviewId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", date='" + date + '\'' +
                ", stars=" + stars +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingReviewPublicResponse that = (GroupTrainingReviewPublicResponse) o;
        return stars == that.stars &&
                Objects.equals(reviewId, that.reviewId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(date, that.date) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, trainingName, date, stars, text);
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getTrainingName() {
        return trainingName;
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
