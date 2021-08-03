package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class GroupTrainingResponseOld {

    @NotNull
    @JsonProperty("id")
    private final String trainingId;
    @NotNull
    @JsonProperty("title")
    private final String trainingName;
    @NotNull
    private final List<UserResponse> trainers;
    @NotNull
    private final String startDate;
    @NotNull
    private final String endDate;
    @NotNull
    private final boolean allDay;
    @NotNull
    private final String location;
    @NotNull
    private final int limit;
    @NotNull
    private final double rating;
    @NotNull
    private final List<UserResponse> participants;
    @NotNull
    private final List<UserResponse> reserveList;

    public GroupTrainingResponseOld(
            @JsonProperty("id") String trainingId,
            @JsonProperty("title") String trainingName,
            List<UserResponse> trainers,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String location,
            int limit,
            double rating,
            List<UserResponse> participants,
            List<UserResponse> reserveList
    ) throws InvalidHourException, InvalidDateException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (!DateValidator.validate(startDate.format(dateFormatter))
                || !Time24HoursValidator.validate(startDate.format(timeFormatter)))
            throw new InvalidDateException("Wrong start date or time");

        if (!DateValidator.validate(endDate.format(dateFormatter))
                || !Time24HoursValidator.validate(endDate.format(timeFormatter)))
            throw new InvalidHourException("Wrong end date or time");

        this.trainingId = trainingId;
        this.trainingName = trainingName;
        this.trainers = trainers;
        this.startDate = startDate.format(dateFormatter).concat("T").concat(startDate.format(timeFormatter));
        this.endDate = endDate.format(dateFormatter).concat("T").concat(endDate.format(timeFormatter));
        this.allDay = false;
        this.location = location;
        this.limit = limit;
        this.rating = rating;
        this.participants = participants;
        this.reserveList = reserveList;
    }

    @Override
    public String toString() {
        return "GroupTrainingResponse{" +
                "trainingId='" + trainingId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", trainers='" + trainers + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", location=" + location +
                ", limit=" + limit +
                ", rating=" + rating +
                ", participants=" + participants +
                ", reserveList=" + reserveList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupTrainingResponseOld that = (GroupTrainingResponseOld) o;
        return allDay == that.allDay &&
                limit == that.limit &&
                Double.compare(that.rating, rating) == 0 &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(trainers, that.trainers) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(location, that.location) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                trainingId,
                trainingName,
                trainers,
                startDate,
                endDate,
                allDay,
                location,
                limit,
                rating,
                participants,
                reserveList
        );
    }

    public String getTrainingId() {
        return trainingId;
    }

    public String getTrainingName() {
        return trainingName;
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

    public boolean isAllDay() {
        return allDay;
    }

    public String getLocation() {
        return location;
    }

    public int getLimit() {
        return limit;
    }

    public double getRating() {
        return rating;
    }

    public List<UserResponse> getParticipants() {
        return participants;
    }

    public List<UserResponse> getReserveList() {
        return reserveList;
    }
}
