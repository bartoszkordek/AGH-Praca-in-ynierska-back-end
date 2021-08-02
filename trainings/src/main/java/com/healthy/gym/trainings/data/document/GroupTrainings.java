package com.healthy.gym.trainings.data.document;

import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

import static com.healthy.gym.trainings.utils.Time24HoursValidator.validate;

@Document(collection = "GroupTrainings")
public class GroupTrainings {

    @Id
    private String id;
    private String trainingId;
    @DBRef
    private TrainingTypeDocument trainingType;
    @DBRef
    private List<UserDocument> trainers;
    private String date;
    private String startTime;
    private String endTime;
    private int hallNo;
    private int limit;
    @DBRef
    private List<UserDocument> participants;
    @DBRef
    private List<UserDocument> reserveList;

    public GroupTrainings() {

    }

    public GroupTrainings(
            String trainingId,
            TrainingTypeDocument trainingType,
            List<UserDocument> trainers,
            String date,
            String startTime,
            String endTime,
            int hallNo,
            int limit,
            List<UserDocument> participants,
            List<UserDocument> reserveList
    ) throws InvalidHourException {

        if (!validate(startTime)) throw new InvalidHourException("Wrong start time");
        if (!validate(endTime)) throw new InvalidHourException("Wrong end time");

        this.trainingId = trainingId;
        this.trainingType = trainingType;
        this.trainers = trainers;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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
                ", trainers='" + trainers + '\'' +
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
                Objects.equals(trainers, that.trainers) &&
                Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                trainingId,
                trainingType,
                trainers,
                date,
                startTime,
                endTime,
                hallNo,
                limit,
                participants,
                reserveList
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public TrainingTypeDocument getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingTypeDocument trainingType) {
        this.trainingType = trainingType;
    }

    public List<UserDocument> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<UserDocument> trainers) {
        this.trainers = trainers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) throws InvalidHourException {
        if (!validate(startTime)) throw new InvalidHourException("Wrong start time");
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) throws InvalidHourException {
        if (!validate(endTime)) throw new InvalidHourException("Wrong end time");
        this.endTime = endTime;
    }

    public int getHallNo() {
        return hallNo;
    }

    public void setHallNo(int hallNo) {
        this.hallNo = hallNo;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<UserDocument> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserDocument> participants) {
        this.participants = participants;
    }

    public List<UserDocument> getReserveList() {
        return reserveList;
    }

    public void setReserveList(List<UserDocument> reserveList) {
        this.reserveList = reserveList;
    }
}
