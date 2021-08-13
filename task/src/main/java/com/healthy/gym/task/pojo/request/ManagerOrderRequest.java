package com.healthy.gym.task.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.task.validation.ValidDateFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerOrderRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 20, message = "{field.name.failure}")
    private String title;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 200, message = "{field.name.failure}")
    private String description;

    @NotNull(message = "{field.required}")
    @ValidDateFormat
    private String dueDate;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "ManagerOrderRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerOrderRequest that = (ManagerOrderRequest) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(dueDate, that.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, dueDate);
    }
}
