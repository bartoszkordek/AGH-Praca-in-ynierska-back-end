package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupTrainingModel {

    private Object id;
    private String trainingName;
    private String trainerId;
    private String date;
    private String startTime;
    private String endTime;
    private int hallNo;
    private int limit;

    public GroupTrainingModel(@JsonProperty("_id") Object id,
                              @JsonProperty("training_name") String trainingName,
                              @JsonProperty("trainerId") String trainerId,
                              @JsonProperty("date") String date,
                              @JsonProperty("start_time") String startTime,
                              @JsonProperty("end_time") String endTime,
                              @JsonProperty("hall_no") int hallNo,
                              @JsonProperty("limit") int limit){
        this.id = id;
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallNo = hallNo;
        this.limit = limit;
    }

    public Object getId() {
        return id;
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
}
