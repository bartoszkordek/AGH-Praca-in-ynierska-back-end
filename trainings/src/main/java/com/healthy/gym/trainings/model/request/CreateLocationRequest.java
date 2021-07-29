package com.healthy.gym.trainings.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLocationRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 20, message = "{field.name.failure}")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateLocationRequest that = (CreateLocationRequest) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CreateLocationRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
