package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.trainings.validation.ValidDateTimeFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerGroupTrainingRequest {

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private String trainingTypeId;

    @NotNull(message = "{field.required}")
    private List<String> trainerIds;

    @NotNull(message = "{field.required}")
    @ValidDateTimeFormat
    private String startDate;

    @NotNull(message = "{field.required}")
    @ValidDateTimeFormat
    private String endDate;

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private String locationId;

    @NotNull(message = "{field.required}")
    @Min(value = 1, message = "{field.training.limit.min.value}")
    private int limit;

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(String trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public List<String> getTrainerIds() {
        return trainerIds;
    }

    public void setTrainerIds(List<String> trainerIds) {
        this.trainerIds = trainerIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerGroupTrainingRequest that = (ManagerGroupTrainingRequest) o;
        return limit == that.limit
                && Objects.equals(trainingTypeId, that.trainingTypeId)
                && Objects.equals(trainerIds, that.trainerIds)
                && Objects.equals(startDate, that.startDate)
                && Objects.equals(endDate, that.endDate)
                && Objects.equals(locationId, that.locationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingTypeId, trainerIds, startDate, endDate, locationId, limit);
    }

    @Override
    public String toString() {
        return "CreateGroupTrainingRequest{" +
                "trainingTypeId='" + trainingTypeId + '\'' +
                ", trainerIds=" + trainerIds +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", locationId='" + locationId + '\'' +
                ", limit=" + limit +
                '}';
    }
}
