package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingTypeRequest request = (TrainingTypeRequest) o;
        return Objects.equals(name, request.name) && Objects.equals(description, request.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "TrainingTypeRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
