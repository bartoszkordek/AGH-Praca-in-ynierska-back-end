package com.healthy.gym.trainings.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.response.AbstractResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupTrainingEnrollmentDTO extends AbstractResponse {

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

    public GroupTrainingEnrollmentDTO(

            @JsonProperty("id") String trainingId,
            @JsonProperty("title") String trainingName,
            List<UserResponse> trainers,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String location
    ) throws InvalidDateException, InvalidHourException {
        this.trainingId = trainingId;
        this.trainingName = trainingName;
        this.trainers = trainers;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        if (!DateValidator.validate(startDate.format(dateFormatter))
                || !Time24HoursValidator.validate(startDate.format(timeFormatter)))
            throw new InvalidDateException("Wrong start date or time");

        if (!DateValidator.validate(endDate.format(dateFormatter))
                || !Time24HoursValidator.validate(endDate.format(timeFormatter)))
            throw new InvalidHourException("Wrong end date or time");

        this.startDate = startDate.format(dateFormatter).concat("T").concat(startDate.format(timeFormatter));
        this.endDate = endDate.format(dateFormatter).concat("T").concat(endDate.format(timeFormatter));
        this.allDay = false;
        this.location = location;
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

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String
    toString() {
        return "GroupTrainingEnrollmentResponse{" +
                "trainingId='" + trainingId + '\'' +
                ", trainingName='" + trainingName + '\'' +
                ", trainers=" + trainers +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroupTrainingEnrollmentDTO that = (GroupTrainingEnrollmentDTO) o;
        return allDay == that.allDay &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(trainers, that.trainers) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                trainingId,
                trainingName,
                trainers,
                startDate,
                endDate,
                allDay,
                location);
    }
}
