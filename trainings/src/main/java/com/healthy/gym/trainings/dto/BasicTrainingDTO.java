package com.healthy.gym.trainings.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicTrainingDTO {

    @JsonProperty("id")
    private String trainingId;
    private String title;
    private String startDate;
    private String location;

    public BasicTrainingDTO(){}

    public BasicTrainingDTO(
            String title,
            String startDate,
            String location
    ){
        this.title = title;
        this.startDate = startDate;
        this.location = location;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public String getTitle() {
        return title;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getLocation() {
        return location;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BasicTrainingDTO{" +
                "trainingId='" + trainingId + '\'' +
                ", title='" + title + '\'' +
                ", startDate='" + startDate + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicTrainingDTO that = (BasicTrainingDTO) o;
        return Objects.equals(trainingId, that.trainingId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                trainingId,
                title,
                startDate,
                location
        );
    }
}
