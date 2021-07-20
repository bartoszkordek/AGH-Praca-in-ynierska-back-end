package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validation.DateValidator;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class GroupTrainingPublicResponse {

    @NotNull
    private String trainingId;
    @NotNull
    private String trainingName;
    @NotNull
    private String trainerId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String date;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    @NotNull
    private int hallNo;
    @NotNull
    private int limit;

    public GroupTrainingPublicResponse(
            @JsonProperty("trainingId") String  trainingId,
            @JsonProperty("trainingName") String trainingName,
            @JsonProperty("trainerId") String trainerId,
            @JsonProperty("date") String date,
            @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime,
            @JsonProperty("hallNo") int hallNo,
            @JsonProperty("limit") int limit
    ) throws InvalidHourException, InvalidDateException {

        DateValidator dateValidator = new DateValidator();
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainingId = trainingId;
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        if (dateValidator.validate(date)) {
            this.date = date;
        } else {
            throw new InvalidDateException("Wrong date");
        }
        if (time24HoursValidator.validate(startTime)) {
            this.startTime = startTime;
        } else {
            throw new InvalidHourException("Wrong start time");
        }
        if (time24HoursValidator.validate(endTime)) {
            this.endTime = endTime;
        } else {
            throw new InvalidHourException("Wrong end time");
        }
        this.hallNo = hallNo;
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "GroupTrainingPublicResponse{" +
                "trainingId='" + trainingId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", trainerId='" + trainerId + '\'' +
                ", date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", hallNo=" + hallNo +
                ", limit=" + limit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingPublicResponse that = (GroupTrainingPublicResponse) o;
        return hallNo == that.hallNo &&
                limit == that.limit &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(trainerId, that.trainerId) &&
                Objects.equals(date, that.date) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingId, trainingName, trainerId, date, startTime, endTime, hallNo, limit);
    }

    public String getTrainingId() { return trainingId; }

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
