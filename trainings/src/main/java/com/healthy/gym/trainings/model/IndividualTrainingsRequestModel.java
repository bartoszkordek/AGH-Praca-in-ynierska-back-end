package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndividualTrainingsRequestModel {

    private String trainerId;
    private String date;
    private String startTime;
    private String endTime;
    private String remarks;

    public IndividualTrainingsRequestModel(@JsonProperty("trainerId") String trainerId,
                                           @JsonProperty("date") String date,
                                           @JsonProperty("startTime") String startTime,
                                           @JsonProperty("endTime") String endTime,
                                           @JsonProperty("remarks") String remarks){
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remarks = remarks;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRemarks() {
        return remarks;
    }
}
