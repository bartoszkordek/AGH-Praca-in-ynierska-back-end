package com.healthy.gym.trainings.data.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "GroupTrainingsReviews")
public class GroupTrainingsReviews {

    @Id
    @JsonProperty("_id")
    private String id;
    private String reviewId;
    private String trainingTypeId;
    // + Avatar TBC
    private String clientId;
    private String date;
    private int stars;
    private String text;

    public GroupTrainingsReviews() {

    }

    public GroupTrainingsReviews(
            String reviewId,
            String trainingTypeId,
            String clientId,
            String date,
            int stars,
            String text
    ) {
        this.reviewId = reviewId;
        this.trainingTypeId = trainingTypeId;
        this.clientId = clientId;
        this.date = date;
        this.stars = stars;
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupTrainingsReviews{" +
                "id='" + id + '\'' +
                ", reviewId='" + reviewId + '\'' +
                ", trainingTypeId='" + trainingTypeId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", date='" + date + '\'' +
                ", stars=" + stars +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingsReviews that = (GroupTrainingsReviews) o;
        return stars == that.stars
                && Objects.equals(id, that.id)
                && Objects.equals(reviewId, that.reviewId)
                && Objects.equals(trainingTypeId, that.trainingTypeId)
                && Objects.equals(clientId, that.clientId)
                && Objects.equals(date, that.date)
                && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reviewId, trainingTypeId, clientId, date, stars, text);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(String trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
