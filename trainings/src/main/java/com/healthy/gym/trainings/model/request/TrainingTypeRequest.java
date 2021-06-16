package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.trainings.validation.ValidDurationFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingTypeRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 1000, message = "{field.required}")
    private String name;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 1000, message = "{field.required}")
    private String description;

    @NotNull(message = "{field.required}")
    @ValidDurationFormat(message = "{exception.duration.format}")
    private String duration;

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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypeRequest request = (TrainingTypeRequest) o;
        return Objects.equals(name, request.name)
                && Objects.equals(description, request.description)
                && Objects.equals(duration, request.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, duration);
    }

    @Override
    public String toString() {
        return "TrainingTypeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
