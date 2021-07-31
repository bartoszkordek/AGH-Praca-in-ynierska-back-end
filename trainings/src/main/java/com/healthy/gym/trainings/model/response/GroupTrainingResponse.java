package com.healthy.gym.trainings.model.response;

import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.validation.DateValidator;
import com.healthy.gym.trainings.validation.Time24HoursValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class GroupTrainingResponse {

    @NotNull
    private final String trainingId;
    @NotNull
    private final String trainingName;
    @NotNull
    private final String trainerId;
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
    @NotNull
    private final double rating;
    @NotNull
    private final List<ParticipantsResponse> participants;
    @NotNull
    private final List<ParticipantsResponse> reserveList;

    public GroupTrainingResponse(
            String trainingId,
            String trainingName,
            String trainerId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
            String startTime,
            String endTime,
            int hallNo,
            int limit,
            double rating,
            List<ParticipantsResponse> participants,
            List<ParticipantsResponse> reserveList
    ) throws InvalidHourException, InvalidDateException {

        if (!DateValidator.validate(date) || !Time24HoursValidator.validate(startTime))
            throw new InvalidDateException("Wrong start date or time");

        if (!DateValidator.validate(date) || !Time24HoursValidator.validate(endTime))
            throw new InvalidHourException("Wrong end date or time");

        this.trainingId = trainingId;
        this.trainingName = trainingName;
        this.trainerId = trainerId;
        this.startDate = date.concat("T").concat(startTime);
        this.endDate = date.concat("T").concat(endTime);
        this.allDay = false;
        this.hallNo = hallNo;
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
                ", trainerId='" + trainerId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", allDay=" + allDay +
                ", hallNo=" + hallNo +
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
        GroupTrainingResponse that = (GroupTrainingResponse) o;
        return allDay == that.allDay &&
                hallNo == that.hallNo &&
                limit == that.limit &&
                Double.compare(that.rating, rating) == 0 &&
                Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(trainingName, that.trainingName) &&
                Objects.equals(trainerId, that.trainerId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(reserveList, that.reserveList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                trainingId,
                trainingName,
                trainerId,
                startDate,
                endDate,
                allDay,
                hallNo,
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

    public String getTrainerId() {
        return trainerId;
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

    public int getHallNo() {
        return hallNo;
    }

    public int getLimit() {
        return limit;
    }

    public double getRating() {
        return rating;
    }

    public List<ParticipantsResponse> getParticipants() {
        return participants;
    }

    public List<ParticipantsResponse> getReserveList() {
        return reserveList;
    }
}
