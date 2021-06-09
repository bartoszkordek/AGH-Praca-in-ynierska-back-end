package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndividualTrainingsAcceptModel {

    private int hallNo;

    public IndividualTrainingsAcceptModel(@JsonProperty("hallNo") int hallNo){
        this.hallNo = hallNo;
    }

    public int getHallNo() {
        return hallNo;
    }
}
