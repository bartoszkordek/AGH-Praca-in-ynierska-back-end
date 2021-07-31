package com.healthy.gym.trainings.model.request;

import java.util.Objects;

public class GroupTrainingReviewUpdateRequest {

    private int stars;
    private String text;

    public GroupTrainingReviewUpdateRequest() {
    }

    public GroupTrainingReviewUpdateRequest(int stars, String text) {
        this.stars = stars;
        this.text = text;
    }

    @Override
    public String toString() {
        return "GroupTrainingReviewUpdateRequest{" +
                "stars=" + stars +
                ", text='" + text + '\'' +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingReviewUpdateRequest that = (GroupTrainingReviewUpdateRequest) o;
        return stars == that.stars && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stars, text);
    }
}
