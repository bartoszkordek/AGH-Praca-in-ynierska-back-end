package com.healthy.gym.trainings.data.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "GroupTrainings")
public class GroupTrainings {

    @Id
    @JsonProperty("_id")
    private String id;

    @JsonProperty("trainingId")
    private String trainingId;

    @DBRef
    @JsonProperty("trainingType")
    private TrainingTypeDocument trainingType;
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
    @DBRef
    @JsonProperty("participants")
    private List<UserDocument> participants;
    @DBRef
    @JsonProperty("reserveList")
    private List<UserDocument> reserveList;

    public GroupTrainings(){

    }

    public GroupTrainings(String trainingId, TrainingTypeDocument trainingType, String trainerId, String date,
                          String startTime, String endTime, int hallNo, int limit, List<UserDocument> participants,
                          List<UserDocument> reserveList)
            throws InvalidHourException {
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainingId = trainingId;
        this.trainingType = trainingType;
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
                ", trainingId='" + trainingId + '\'' +
                ", trainingType='" + trainingType + '\'' +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainings that = (GroupTrainings) o;
        return hallNo == that.hallNo &&
                limit == that.limit &&
                Objects.equals(id, that.id) &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingType, that.trainingType) &&
                Objects.equals(trainerId, that.trainerId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainingId, trainingType, trainerId, date, startTime, endTime, hallNo, limit, participants, reserveList);
    }

    public String getId() {
        return id;
    }

    public String getTrainingId() { return trainingId; }

    public TrainingTypeDocument getTrainingType() {
        return trainingType;
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

    public List<UserDocument> getParticipants() {
        return participants;
    }

    public List<UserDocument> getReserveList() {
        return reserveList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTrainingId(String trainingId) { this.trainingId = trainingId; }

    public void setTrainingType(TrainingTypeDocument trainingType) {
        this.trainingType = trainingType;
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

    public void setParticipants(List<UserDocument> participants) {
        this.participants = participants;
    }

    public void setReserveList(List<UserDocument> reserveList) {
        this.reserveList = reserveList;
    }
}
