package com.healthy.gym.trainings.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingTypeDTO {

    private String trainingTypeId;
    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime duration;

    @JsonProperty("image")
    private String imageUrl;

    public TrainingTypeDTO() {
    }

    public TrainingTypeDTO(String trainingTypeId, String name, String description, LocalTime duration, String imageUrl) {
        this.trainingTypeId = trainingTypeId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.imageUrl = imageUrl;
    }

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(String trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypeDTO that = (TrainingTypeDTO) o;
        return Objects.equals(trainingTypeId, that.trainingTypeId)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(duration, that.duration)
                && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingTypeId, name, description, duration, imageUrl);
    }

    @Override
    public String toString() {
        return "TrainingTypeDTO{" +
                "trainingTypeId='" + trainingTypeId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration='" + duration + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
