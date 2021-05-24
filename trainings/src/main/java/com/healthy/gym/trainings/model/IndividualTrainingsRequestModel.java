package com.healthy.gym.trainings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.validator.DateValidator;
import com.healthy.gym.trainings.validator.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

public class IndividualTrainingsRequestModel {

    @NotNull
    private String trainerId;
    @NotNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String date;
    @NotNull
    private String startTime;
    @NotNull
    private String endTime;
    private String remarks;

    public IndividualTrainingsRequestModel(@JsonProperty("trainerId") String trainerId,
                                           @JsonProperty("date") String date,
                                           @JsonProperty("startTime") String startTime,
                                           @JsonProperty("endTime") String endTime,
                                           @JsonProperty("remarks") String remarks) throws InvalidDateException, InvalidHourException {
        DateValidator dateValidator = new DateValidator();
        Time24HoursValidator time24HoursValidator = new Time24HoursValidator();
        this.trainerId = trainerId;
        if(dateValidator.validate(date)){
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
        this.remarks = remarks;
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

    public String getRemarks() {
        return remarks;
    }
}
