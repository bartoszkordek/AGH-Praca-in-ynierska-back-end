package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validation.DateValidator;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;

public class GroupTrainingResponse {

    @NotNull
    private String trainingId;
    @NotNull
    private String trainingTypeId;
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
    @NotNull
    private List<String> participants;
    @NotNull
    private List<String> reserveList;

    public GroupTrainingResponse(String trainingId, String trainingTypeId, String trainerId, String date,
                                 String startTime, String endTime, int hallNo, int limit, List<String> participants,
                                 List<String> reserveList) throws InvalidHourException, InvalidDateException {
        DateValidator dateValidator = new DateValidator();
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainingId = trainingId;
        this.trainingTypeId = trainingTypeId;
        this.trainerId = trainerId;
        if (dateValidator.validate(date)) {
            this.date = date;
        } else {
            throw new InvalidDateException("Wrong date");
        }
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
        return "GroupTrainingResponse{" +
                "trainingId='" + trainingId + '\'' +
                ", trainingTypeId='" + trainingTypeId + '\'' +
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

    public String getTrainingId() {
        return trainingId;
    }

    public String getTrainingTypeId() {
        return trainingTypeId;
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
}
