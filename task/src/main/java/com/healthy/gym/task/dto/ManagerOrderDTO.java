package com.healthy.gym.task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.task.enums.AcceptanceStatus;

import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManagerOrderDTO {

    @JsonProperty("id")
    private String taskId;
    private String title;
    private String description;
    private LocalDate orderDate;
    private LocalDate lastOrderUpdateDate;
    private LocalDate dueDate;
    private AcceptanceStatus employeeAccept;

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public LocalDate getLastOrderUpdateDate() {
        return lastOrderUpdateDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public AcceptanceStatus getEmployeeAccept() {
        return employeeAccept;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setLastOrderUpdateDate(LocalDate lastOrderUpdateDate) {
        this.lastOrderUpdateDate = lastOrderUpdateDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setEmployeeAccept(AcceptanceStatus employeeAccept) {
        this.employeeAccept = employeeAccept;
    }

    @Override
    public String toString() {
        return "ManagerOrderDTO{" +
                "taskId='" + taskId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", orderDate=" + orderDate +
                ", lastOrderUpdateDate=" + lastOrderUpdateDate +
                ", dueDate=" + dueDate +
                ", employeeAccept=" + employeeAccept +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerOrderDTO that = (ManagerOrderDTO) o;
        return Objects.equals(taskId, that.taskId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(orderDate, that.orderDate) &&
                Objects.equals(lastOrderUpdateDate, that.lastOrderUpdateDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                employeeAccept == that.employeeAccept;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                taskId,
                title,
                description,
                orderDate,
                lastOrderUpdateDate,
                dueDate,
                employeeAccept
        );
    }
}
