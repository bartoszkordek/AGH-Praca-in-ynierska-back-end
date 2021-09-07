package com.healthy.gym.task.pojo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthy.gym.task.validation.ValidDateTimeFormat;
import com.healthy.gym.task.validation.ValidIDFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerTaskCreationRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 20, message = "{field.title.failure}")
    private String title;

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 200, message = "{field.description.failure}")
    private String description;

    @NotNull(message = "{field.required}")
    @ValidIDFormat
    private String employeeId;

    @NotNull(message = "{field.required}")
    @ValidDateTimeFormat
    private String dueDate;

    private String reminderDate;

    private String priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ManagerOrderRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", reminderDate='" + reminderDate + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerTaskCreationRequest that = (ManagerTaskCreationRequest) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(reminderDate, that.reminderDate) &&
                Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                title,
                description,
                employeeId,
                dueDate,
                reminderDate,
                priority
        );
    }
}
