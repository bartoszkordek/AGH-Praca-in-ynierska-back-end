package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
}
