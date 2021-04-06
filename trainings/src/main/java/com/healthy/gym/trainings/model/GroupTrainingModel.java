package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GroupTrainingModel {

    private String trainingName;
    private String trainerId;
    private String date;
    private String startTime;
    private String endTime;
    private int hallNo;
    private int limit;
    private List<String> participants;

    public GroupTrainingModel(@JsonProperty("trainingName") String trainingName,
                              @JsonProperty("trainerId") String trainerId,
                              @JsonProperty("date") String date,
                              @JsonProperty("startTime") String startTime,
                              @JsonProperty("endTime") String endTime,
                              @JsonProperty("hallNo") int hallNo,
                              @JsonProperty("limit") int limit,
                              @JsonProperty("participants") List<String> participants){
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallNo = hallNo;
        this.limit = limit;
        this.participants = participants;
    }

    public String getTrainingName() {
        return trainingName;
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

    public int getHallNo() {
        return hallNo;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
