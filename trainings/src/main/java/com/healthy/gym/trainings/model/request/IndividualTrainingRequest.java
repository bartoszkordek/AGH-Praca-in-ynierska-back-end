package com.healthy.gym.trainings.model.request;

import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

public class IndividualTrainingRequest {

    @NotNull
    private final String trainerId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final String date;
    @NotNull
    private final String startTime;
    @NotNull
    private final String endTime;
    private final String remarks;

    public IndividualTrainingRequest(
            String trainerId,
            String date,
            String startTime,
            String endTime,
            String remarks
    ) throws InvalidDateException, InvalidHourException {

        if (!DateValidator.validate(date)) throw new InvalidDateException("Wrong date");
        if (!Time24HoursValidator.validate(startTime)) throw new InvalidDateException("Wrong date");
        if (!Time24HoursValidator.validate(endTime)) throw new InvalidHourException("Wrong end time");

        this.trainerId = trainerId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
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
