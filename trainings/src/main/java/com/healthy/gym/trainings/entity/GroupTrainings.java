package com.healthy.gym.trainings.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "GroupTrainings")
public class GroupTrainings {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("trainingName")
    private String trainingName;
    @JsonProperty("trainerId")
    private String trainerId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("endTime")
    private String endTime;
    @JsonProperty("hallNo")
    private int hallNo;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("participants")
    private List<String> participants;

    public GroupTrainings(){

    }

    public GroupTrainings(String trainingName, String trainerId, String date, String startTime, String endTime,
                          int hallNo, int limit, List<String> participants){
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hallNo = hallNo;
        this.limit = limit;
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "GroupTrainings{" +
                "id='" + id + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", hallNo=" + hallNo +
                ", limit=" + limit +
                ", participants=" + participants +
                '}';
    }

    public String getId() {
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

    public List<String> getParticipants() {
        return participants;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setHallNo(int hallNo) {
        this.hallNo = hallNo;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
