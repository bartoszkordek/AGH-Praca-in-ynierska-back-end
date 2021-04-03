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

    @JsonProperty("training_name")
    private String training_name;
    @JsonProperty("trainerId")
    private String trainerId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("start_time")
    private String start_time;
    @JsonProperty("end_time")
    private String end_time;
    @JsonProperty("hall_no")
    private int hall_no;
    @JsonProperty("limit")
    private int limit;
    @JsonProperty("participants")
    private List<String> participants;

    public GroupTrainings(){

    }

    public GroupTrainings(String training_name, String trainerId, String date, String start_time, String end_time,
                          int hall_no, int limit, List<String> participants){
        this.training_name = training_name;
        this.trainerId = trainerId;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.hall_no = hall_no;
        this.limit = limit;
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "GroupTrainings{" +
                "id='" + id + '\'' +
                ", training_name='" + training_name + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", hall_no=" + hall_no +
                ", limit=" + limit +
                ", participants=" + participants +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTraining_name() {
        return training_name;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getDate() {
        return date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public int getHall_no() {
        return hall_no;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> getParticipants() {
        return participants;
    }
}
