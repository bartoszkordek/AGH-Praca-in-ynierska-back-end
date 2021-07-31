package com.healthy.gym.trainings.model.response;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class GroupTrainingReviewPublicResponse {

    @NotNull
    private final String reviewId;
    @NotNull
    private final String trainingTypeId;
    @NotNull
    private final String date;
    private final int stars;
    @NotNull
    private final String text;

    public GroupTrainingReviewPublicResponse(
            @NotNull final String reviewId,
            @NotNull final String trainingTypeId,
            @NotNull final String date,
            @NotNull final int stars,
            @NotNull final String text
    ) {
        this.reviewId = reviewId;
        this.trainingTypeId = trainingTypeId;
        this.date = date;
        this.stars = stars;
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupTrainingReviewPublicResponse{" +
                "reviewId='" + reviewId + '\'' +
                ", trainingTypeId='" + trainingTypeId + '\'' +
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
                Objects.equals(trainingTypeId, that.trainingTypeId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, trainingTypeId, date, stars, text);
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getTrainingTypeId() {
        return trainingTypeId;
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
