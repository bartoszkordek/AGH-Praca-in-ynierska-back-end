package com.healthy.gym.task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.Priority;

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
    private LocalDate lastTaskUpdateDate;
    private LocalDate dueDate;
    private LocalDate reminderDate;
    private LocalDate reportDate;
    private Priority priority;
    private int mark;
    private AcceptanceStatus employeeAccept;
    private AcceptanceStatus managerAccept;
    private String employeeComment;

    public TaskDTO() { }

    public TaskDTO(
            String taskId,
            BasicUserInfoDTO manager,
            BasicUserInfoDTO employee,
            String title,
            String description,
            String report,
            LocalDate orderDate,
            LocalDate lastTaskUpdateDate,
            LocalDate dueDate,
            LocalDate reminderDate,
            LocalDate reportDate,
            Priority priority,
            int mark,
            AcceptanceStatus employeeAccept,
            AcceptanceStatus managerAccept,
            String employeeComment
    ){
        this.taskId = taskId;
        this.manager = manager;
        this.employee = employee;
        this.title = title;
        this.description = description;
        this.report = report;
        this.orderDate = orderDate;
        this.lastTaskUpdateDate = lastTaskUpdateDate;
        this.dueDate = dueDate;
        this.reminderDate = reminderDate;
        this.reportDate = reportDate;
        this.priority = priority;
        this.mark = mark;
        this.employeeAccept = employeeAccept;
        this.managerAccept = managerAccept;
        this.employeeComment = employeeComment;
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

    public LocalDate getLastTaskUpdateDate() {
        return lastTaskUpdateDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getMark() {
        return mark;
    }

    public AcceptanceStatus getEmployeeAccept() {
        return employeeAccept;
    }

    public AcceptanceStatus getManagerAccept() {
        return managerAccept;
    }

    public String getEmployeeComment() {
        return employeeComment;
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

    public void setLastTaskUpdateDate(LocalDate lastTaskUpdateDate) {
        this.lastTaskUpdateDate = lastTaskUpdateDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public void setEmployeeAccept(AcceptanceStatus employeeAccept) {
        this.employeeAccept = employeeAccept;
    }

    public void setManagerAccept(AcceptanceStatus managerAccept) {
        this.managerAccept = managerAccept;
    }

    public void setEmployeeComment(String employeeComment) {
        this.employeeComment = employeeComment;
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
                ", lastTaskUpdateDate=" + lastTaskUpdateDate +
                ", dueDate=" + dueDate +
                ", reminderDate=" + reminderDate +
                ", reportDate=" + reportDate +
                ", priority=" + priority +
                ", mark=" + mark +
                ", employeeAccept=" + employeeAccept +
                ", managerAccept=" + managerAccept +
                ", employeeComment='" + employeeComment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return mark == taskDTO.mark &&
                Objects.equals(taskId, taskDTO.taskId) &&
                Objects.equals(manager, taskDTO.manager) &&
                Objects.equals(employee, taskDTO.employee) &&
                Objects.equals(title, taskDTO.title) &&
                Objects.equals(description, taskDTO.description) &&
                Objects.equals(report, taskDTO.report) &&
                Objects.equals(orderDate, taskDTO.orderDate) &&
                Objects.equals(lastTaskUpdateDate, taskDTO.lastTaskUpdateDate) &&
                Objects.equals(dueDate, taskDTO.dueDate) &&
                Objects.equals(reminderDate, taskDTO.reminderDate) &&
                Objects.equals(reportDate, taskDTO.reportDate) &&
                priority == taskDTO.priority &&
                employeeAccept == taskDTO.employeeAccept &&
                managerAccept == taskDTO.managerAccept &&
                Objects.equals(employeeComment, taskDTO.employeeComment);
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
                lastTaskUpdateDate,
                dueDate,
                reminderDate,
                reportDate,
                priority,
                mark,
                employeeAccept,
                managerAccept,
                employeeComment
        );
    }
}
