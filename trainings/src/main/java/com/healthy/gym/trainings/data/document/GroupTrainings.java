package com.healthy.gym.trainings.data.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
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
    @JsonProperty("reserveList")
    private List<String> reserveList;

    public GroupTrainings(){

    }

    public GroupTrainings(String trainingName, String trainerId, String date, String startTime, String endTime,
                          int hallNo, int limit, List<String> participants, List<String> reserveList) throws InvalidHourException {
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        this.date = date;
        if(time24HoursValidator.validate(startTime)){
            this.startTime = startTime;
        } else {
            throw new InvalidHourException("Wrong start time");
        }
        if(time24HoursValidator.validate(endTime)){
            this.endTime = endTime;
        } else {
            throw new InvalidHourException("Wrong end time");
        }
        this.hallNo = hallNo;
        this.limit = limit;
        this.participants = participants;
        this.reserveList = reserveList;
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
                ", reserveList=" + reserveList +
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

    public List<String> getReserveList() {
        return reserveList;
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

    public void setStartTime(String startTime) throws InvalidHourException {
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        if(time24HoursValidator.validate(startTime)){
            this.startTime = startTime;
        } else {
            throw new InvalidHourException("Wrong start time");
        }
    }

    public void setEndTime(String endTime) throws InvalidHourException {

        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        if(time24HoursValidator.validate(endTime)){
            this.endTime = endTime;
        } else {
            throw new InvalidHourException("Wrong end time");
        }
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

    public void setReserveList(List<String> reserveList) {
        this.reserveList = reserveList;
    }
}
