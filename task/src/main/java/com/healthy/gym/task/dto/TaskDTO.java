package com.healthy.gym.task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.task.enums.AcceptanceStatus;

import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {

    @JsonProperty("id")
    private String taskId;
    private BasicUserInfoDTO manager;
    private BasicUserInfoDTO employee;
    private String title;
    private String description;
    private String report;
    private LocalDate orderDate;
    private LocalDate lastOrderUpdateDate;
    private LocalDate dueDate;
    private LocalDate reportDate;
    private AcceptanceStatus employeeAccept;
    private AcceptanceStatus managerAccept;

    public TaskDTO() { }

    public TaskDTO(
            String taskId,
            BasicUserInfoDTO manager,
            BasicUserInfoDTO employee,
            String title,
            String description,
            String report,
            LocalDate orderDate,
            LocalDate lastOrderUpdateDate,
            LocalDate dueDate,
            LocalDate reportDate,
            AcceptanceStatus employeeAccept,
            AcceptanceStatus managerAccept
    ){
        this.taskId = taskId;
        this.manager = manager;
        this.employee = employee;
        this.title = title;
        this.description = description;
        this.report = report;
        this.orderDate = orderDate;
        this.lastOrderUpdateDate = lastOrderUpdateDate;
        this.dueDate = dueDate;
        this.reportDate = reportDate;
        this.employeeAccept = employeeAccept;
        this.managerAccept = managerAccept;
    }


    public String getTaskId() {
        return taskId;
    }

    public BasicUserInfoDTO getManager() {
        return manager;
    }

    public BasicUserInfoDTO getEmployee() {
        return employee;
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

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setManager(BasicUserInfoDTO manager) {
        this.manager = manager;
    }

    public void setEmployee(BasicUserInfoDTO employee) {
        this.employee = employee;
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
        return "TaskDTO{" +
                "taskId='" + taskId + '\'' +
                ", manager=" + manager +
                ", employee=" + employee +
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
        TaskDTO taskDTO = (TaskDTO) o;
        return Objects.equals(taskId, taskDTO.taskId) &&
                Objects.equals(manager, taskDTO.manager) &&
                Objects.equals(employee, taskDTO.employee) &&
                Objects.equals(title, taskDTO.title) &&
                Objects.equals(description, taskDTO.description) &&
                Objects.equals(report, taskDTO.report) &&
                Objects.equals(orderDate, taskDTO.orderDate) &&
                Objects.equals(lastOrderUpdateDate, taskDTO.lastOrderUpdateDate) &&
                Objects.equals(dueDate, taskDTO.dueDate) &&
                Objects.equals(reportDate, taskDTO.reportDate) &&
                employeeAccept == taskDTO.employeeAccept &&
                managerAccept == taskDTO.managerAccept;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                taskId,
                manager,
                employee,
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
