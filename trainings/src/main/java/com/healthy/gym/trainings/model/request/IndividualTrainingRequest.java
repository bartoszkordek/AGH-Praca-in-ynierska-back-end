package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.trainings.validation.ValidDateTimeFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndividualTrainingRequest {

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private final String trainerId;

    @NotNull(message = "{field.required}")
    @ValidDateTimeFormat
    private final String startDateTime;

    @NotNull(message = "{field.required}")
    @ValidDateTimeFormat
    private final String endDateTime;

    @NotNull(message = "{field.required}")
    @Size(max = 280, message = "{field.remarks.size}")
    private final String remarks;

    public IndividualTrainingRequest(
            String trainerId,
            String startDateTime,
            String endDateTime,
            String remarks
    ) {
        this.trainerId = trainerId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.remarks = remarks;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualTrainingRequest that = (IndividualTrainingRequest) o;
        return Objects.equals(trainerId, that.trainerId)
                && Objects.equals(startDateTime, that.startDateTime)
                && Objects.equals(endDateTime, that.endDateTime)
                && Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId, startDateTime, endDateTime, remarks);
    }

    @Override
    public String toString() {
        return "IndividualTrainingRequest{" +
                "trainerId='" + trainerId + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
