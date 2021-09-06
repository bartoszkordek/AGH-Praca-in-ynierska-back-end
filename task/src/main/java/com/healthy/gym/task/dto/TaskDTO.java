package com.healthy.gym.task.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.Priority;

import java.time.LocalDateTime;
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
    private LocalDateTime taskCreationDate;
    private LocalDateTime lastTaskUpdateDate;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;
    private LocalDateTime reportDate;
    private Priority priority;
    private int mark;
    private AcceptanceStatus employeeAccept;
    private AcceptanceStatus managerAccept;
    private String employeeComment;

    public TaskDTO() {
    }

    public TaskDTO(
            String taskId,
            BasicUserInfoDTO manager,
            BasicUserInfoDTO employee,
            String title,
            String description,
            String report,
            LocalDateTime taskCreationDate,
            LocalDateTime lastTaskUpdateDate,
            LocalDateTime dueDate,
            LocalDateTime reminderDate,
            LocalDateTime reportDate,
            Priority priority,
            int mark,
            AcceptanceStatus employeeAccept,
            AcceptanceStatus managerAccept,
            String employeeComment
    ) {
        this.taskId = taskId;
        this.manager = manager;
        this.employee = employee;
        this.title = title;
        this.description = description;
        this.report = report;
        this.taskCreationDate = taskCreationDate;
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

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public BasicUserInfoDTO getManager() {
        return manager;
    }

    public void setManager(BasicUserInfoDTO manager) {
        this.manager = manager;
    }

    public BasicUserInfoDTO getEmployee() {
        return employee;
    }

    public void setEmployee(BasicUserInfoDTO employee) {
        this.employee = employee;
    }

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

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public LocalDateTime getTaskCreationDate() {
        return taskCreationDate;
    }

    public void setTaskCreationDate(LocalDateTime taskCreationDate) {
        this.taskCreationDate = taskCreationDate;
    }

    public LocalDateTime getLastTaskUpdateDate() {
        return lastTaskUpdateDate;
    }

    public void setLastTaskUpdateDate(LocalDateTime lastTaskUpdateDate) {
        this.lastTaskUpdateDate = lastTaskUpdateDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public AcceptanceStatus getEmployeeAccept() {
        return employeeAccept;
    }

    public void setEmployeeAccept(AcceptanceStatus employeeAccept) {
        this.employeeAccept = employeeAccept;
    }

    public AcceptanceStatus getManagerAccept() {
        return managerAccept;
    }

    public void setManagerAccept(AcceptanceStatus managerAccept) {
        this.managerAccept = managerAccept;
    }

    public String getEmployeeComment() {
        return employeeComment;
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
                ", taskCreationDate=" + taskCreationDate +
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
                Objects.equals(taskCreationDate, taskDTO.taskCreationDate) &&
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
                taskCreationDate,
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
