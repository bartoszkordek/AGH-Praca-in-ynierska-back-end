package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndividualTrainingAcceptanceRequest {

    private int hallNo;

    public IndividualTrainingAcceptanceRequest(@JsonProperty("hallNo") int hallNo) {
        this.hallNo = hallNo;
    }

    public int getHallNo() {
        return hallNo;
    }
}
