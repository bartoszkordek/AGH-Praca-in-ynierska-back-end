package com.healthy.gym.task.data.document;

import com.healthy.gym.task.enums.AcceptanceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Objects;

@Document(collection = "tasks")
public class TaskDocument {

    @Id
    private String id;
    private String taskId;
    private String title;
    private String description;
    private String report;
    private LocalDate orderDate;
    private LocalDate lastOrderUpdateDate;
    private LocalDate dueDate;
    private LocalDate reportDate;
    private AcceptanceStatus employeeAccept;
    private AcceptanceStatus managerAccept;

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReport() {
        return report;
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

    public LocalDate getReportDate() {
        return reportDate;
    }

    public AcceptanceStatus getEmployeeAccept() {
        return employeeAccept;
    }

    public AcceptanceStatus getManagerAccept() {
        return managerAccept;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setReport(String report) {
        this.report = report;
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

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setEmployeeAccept(AcceptanceStatus employeeAccept) {
        this.employeeAccept = employeeAccept;
    }

    public void setManagerAccept(AcceptanceStatus managerAccept) {
        this.managerAccept = managerAccept;
    }

    @Override
    public String toString() {
        return "TaskDocument{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", report='" + report + '\'' +
                ", orderDate=" + orderDate +
                ", lastOrderUpdateDate=" + lastOrderUpdateDate +
                ", dueDate=" + dueDate +
                ", reportDate=" + reportDate +
                ", employeeAccept=" + employeeAccept +
                ", managerAccept=" + managerAccept +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDocument that = (TaskDocument) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(report, that.report) &&
                Objects.equals(orderDate, that.orderDate) &&
                Objects.equals(lastOrderUpdateDate, that.lastOrderUpdateDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(reportDate, that.reportDate) &&
                employeeAccept == that.employeeAccept &&
                managerAccept == that.managerAccept;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                taskId,
                title,
                description,
                report,
                orderDate,
                lastOrderUpdateDate,
                dueDate,
                reportDate,
                employeeAccept,
                managerAccept
        );
    }
}
