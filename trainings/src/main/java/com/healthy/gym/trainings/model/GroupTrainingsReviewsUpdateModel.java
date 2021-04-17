package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupTrainingsReviewsUpdateModel {

    private int stars;
    private String text;

    public GroupTrainingsReviewsUpdateModel(@JsonProperty("stars") int stars,
                                            @JsonProperty("text") String text){
        this.stars = stars;
        this.text = text;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }
}
