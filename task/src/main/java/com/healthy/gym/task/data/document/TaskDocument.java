package com.healthy.gym.task.data.document;

import com.healthy.gym.task.enums.AcceptanceStatus;
import com.healthy.gym.task.enums.Priority;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Objects;

@Document(collection = "tasks")
public class TaskDocument {

    @Id
    private String id;
    private String taskId;
    @DBRef
    private UserDocument manager;
    @DBRef
    private UserDocument employee;
    private String title;
    private String description;
    private String report;
    private LocalDate taskCreationDate;
    private LocalDate lastTaskUpdateDate;
    private LocalDate dueDate;
    private LocalDate reminderDate;
    private LocalDate reportDate;
    private Priority priority;
    private int mark;
    private AcceptanceStatus employeeAccept;
    private AcceptanceStatus managerAccept;
    private String employeeComment;

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public UserDocument getManager() {
        return manager;
    }

    public UserDocument getEmployee() {
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

    public LocalDate getTaskCreationDate() {
        return taskCreationDate;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setManager(UserDocument manager) {
        this.manager = manager;
    }

    public void setEmployee(UserDocument employee) {
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

    public void setTaskCreationDate(LocalDate taskCreationDate) {
        this.taskCreationDate = taskCreationDate;
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
        return "TaskDocument{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
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
        TaskDocument that = (TaskDocument) o;
        return mark == that.mark &&
                Objects.equals(id, that.id) &&
                Objects.equals(taskId, that.taskId) &&
                Objects.equals(manager, that.manager) &&
                Objects.equals(employee, that.employee) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(report, that.report) &&
                Objects.equals(taskCreationDate, that.taskCreationDate) &&
                Objects.equals(lastTaskUpdateDate, that.lastTaskUpdateDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(reminderDate, that.reminderDate) &&
                Objects.equals(reportDate, that.reportDate) &&
                priority == that.priority &&
                employeeAccept == that.employeeAccept &&
                managerAccept == that.managerAccept &&
                Objects.equals(employeeComment, that.employeeComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
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
