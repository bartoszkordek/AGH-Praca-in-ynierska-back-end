package com.healthy.gym.trainings.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.trainings.shared.ImageDTO;

import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingTypeResponse extends AbstractResponse {

    @JsonProperty("image")
    private ImageDTO imageDTO;

    private String trainingTypeId;
    private String name;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss.SSS")
    private LocalTime duration;

    public TrainingTypeResponse() {
    }

    public TrainingTypeResponse(
            String message,
            Map<String, String> errors,
            ImageDTO imageDTO,
            String trainingTypeId,
            String name,
            String description,
            LocalTime duration
    ) {
        super(message, errors);
        this.imageDTO = imageDTO;
        this.trainingTypeId = trainingTypeId;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public ImageDTO getImageDTO() {
        return imageDTO;
    }

    public void setImageDTO(ImageDTO imageDTO) {
        this.imageDTO = imageDTO;
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

    public String getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(String trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TrainingTypeResponse that = (TrainingTypeResponse) o;
        return Objects.equals(imageDTO, that.imageDTO)
                && Objects.equals(trainingTypeId, that.trainingTypeId)
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), imageDTO, trainingTypeId, name, description, duration);
    }

    @Override
    public String toString() {
        return "TrainingTypeResponse{" +
                "imageDTO=" + imageDTO +
                ", trainingTypeId='" + trainingTypeId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                "} " + super.toString();
    }
}
