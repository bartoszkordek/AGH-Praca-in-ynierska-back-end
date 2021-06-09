package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupTrainingReviewUpdateRequest {

    private int stars;
    private String text;

    public GroupTrainingReviewUpdateRequest(@JsonProperty("stars") int stars,
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
