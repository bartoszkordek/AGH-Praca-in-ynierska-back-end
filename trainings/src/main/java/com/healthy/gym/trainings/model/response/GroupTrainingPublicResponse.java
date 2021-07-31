package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class GroupTrainingPublicResponse {

    @NotNull
    private final String id;
    @NotNull
    private final String title;
    @NotNull
    private final List<UserResponse> trainers;
    @NotNull
    private final String startDate;
    @NotNull
    private final String endDate;
    @NotNull
    private final boolean allDay;
    @NotNull
    private final int hallNo;
    @NotNull
    private final int limit;
    private final double rating;

    public GroupTrainingPublicResponse(
            String trainingId,
            String trainingName,
            List<UserResponse> trainers,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
            String startTime,
            String endTime,
            int hallNo,
            int limit,
            double rating
    ) throws InvalidHourException, InvalidDateException {
        if (!DateValidator.validate(date) || !Time24HoursValidator.validate(startTime))
            throw new InvalidDateException("Wrong start date or time");

        if (!DateValidator.validate(date) || !Time24HoursValidator.validate(endTime))
            throw new InvalidHourException("Wrong end date or time");

        this.id = trainingId;
        this.title = trainingName;
        this.trainers = trainers;
        this.startDate = date.concat("T").concat(startTime);
        this.endDate = date.concat("T").concat(endTime);
        this.allDay = false;
        this.hallNo = hallNo;
        this.limit = limit;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "GroupTrainingPublicResponse{" +
                "trainingId='" + id + '\'' +
                ", trainingName='" + title + '\'' +
                ", trainers='" + trainers + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", hallNo=" + hallNo +
                ", limit=" + limit +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingPublicResponse that = (GroupTrainingPublicResponse) o;
        return allDay == that.allDay &&
                hallNo == that.hallNo &&
                limit == that.limit &&
                Double.compare(that.rating, rating) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(trainers, that.trainers) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                title,
                trainers,
                startDate,
                endDate,
                allDay,
                hallNo,
                limit,
                rating
        );
    }

    public String getTrainingId() {
        return id;
    }

    public String getTrainingName() {
        return title;
    }

    public List<UserResponse> getTrainers() {
        return trainers;
    }

    public String getStartTime() {
        return startDate;
    }

    public String getEndTime() {
        return endDate;
    }

    public int getHallNo() {
        return hallNo;
    }

    public int getLimit() {
        return limit;
    }

    public double getRating() {
        return rating;
    }
}
