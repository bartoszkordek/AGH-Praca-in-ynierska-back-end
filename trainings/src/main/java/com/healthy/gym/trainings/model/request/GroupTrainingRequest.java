package com.healthy.gym.trainings.model.request;

import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class GroupTrainingRequest {

    @NotNull
    private final String trainingTypeId;
    @NotNull
    private final List<String> trainers;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final String date;
    @NotNull
    private final String startTime;
    @NotNull
    private final String endTime;
    @NotNull
    private final int hallNo;
    @NotNull
    private final int limit;
    private final List<String> participants;
    private final List<String> reserveList;

    public GroupTrainingRequest(
            String trainingTypeId,
            List<String> trainers,
            String date,
            String startTime,
            String endTime,
            int hallNo,
            int limit,
            List<String> participants,
            List<String> reserveList
    ) throws InvalidHourException, InvalidDateException {

        if (!DateValidator.validate(date)) throw new InvalidDateException("Wrong date");
        if (!Time24HoursValidator.validate(startTime)) throw new InvalidDateException("Wrong date");
        if (!Time24HoursValidator.validate(endTime)) throw new InvalidHourException("Wrong end time");

        this.trainingTypeId = trainingTypeId;
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
        return "GroupTrainingRequest{" +
                "trainingTypeId='" + trainingTypeId + '\'' +
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
        GroupTrainingRequest that = (GroupTrainingRequest) o;
        return hallNo == that.hallNo &&
                limit == that.limit &&
                Objects.equals(trainingTypeId, that.trainingTypeId) &&
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
                trainingTypeId,
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

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public List<String> getTrainers() {
        return trainers;
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
}
