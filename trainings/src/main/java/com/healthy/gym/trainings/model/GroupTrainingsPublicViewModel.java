package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validator.Time24HoursValidator;

import java.util.List;

public class GroupTrainingsPublicViewModel {

    private String trainingName;
    private String trainerId;
    private String date;
    private String startTime;
    private String endTime;
    private int hallNo;
    private int limit;

    public GroupTrainingsPublicViewModel(@JsonProperty("trainingName") String trainingName,
                              @JsonProperty("trainerId") String trainerId,
                              @JsonProperty("date") String date,
                              @JsonProperty("startTime") String startTime,
                              @JsonProperty("endTime") String endTime,
                              @JsonProperty("hallNo") int hallNo,
                              @JsonProperty("limit") int limit) throws InvalidHourException {

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
