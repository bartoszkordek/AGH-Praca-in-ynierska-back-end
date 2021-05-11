package com.healthy.gym.trainings.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validator.Time24HoursValidator;
import org.springframework.data.annotation.Id;

public class IndividualTrainings {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("clientId")
    private String clientId;
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
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("accepted")
    private boolean accepted;
    @JsonProperty("declined")
    private boolean declined;

    public IndividualTrainings(){

    }

    public IndividualTrainings(String clientId, String trainerId, String date,
                               String startTime, String endTime, int hallNo, String remarks,
                               boolean accepted, boolean declined) throws InvalidHourException {

        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();

        this.clientId = clientId;
        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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
        this.remarks = remarks;
        this.accepted = accepted;
        this.declined = declined;
    }

    @Override
    public String toString() {
        return "IndividualTrainings{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", hallNo=" + hallNo +
                ", remarks='" + remarks + '\'' +
                ", accepted=" + accepted +
                ", declined=" + declined +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
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

    public String getRemarks() {
        return remarks;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isDeclined(){ return declined; }

    public void setId(String id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
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

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setDeclined(boolean declined) { this.declined = declined; }
}
