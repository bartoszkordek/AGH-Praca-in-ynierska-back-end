package com.healthy.gym.task.pojo.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class EmployeeAcceptDeclineTaskRequest {

    @NotNull(message = "{field.required}")
    private String acceptanceStatus;

    @NotNull(message = "{field.required}")
    @Size(max = 200, message = "{field.employee.comment}")
    private String employeeComment;

    public String getAcceptanceStatus() {
        return acceptanceStatus;
    }

    public void setAcceptanceStatus(String acceptanceStatus) {
        this.acceptanceStatus = acceptanceStatus;
    }

    public String getEmployeeComment() {
        return employeeComment;
    }

    public void setEmployeeComment(String employeeComment) {
        this.employeeComment = employeeComment;
    }

    @Override
    public String toString() {
        return "EmployeeAcceptDeclineTaskRequest{" +
                "acceptanceStatus='" + acceptanceStatus + '\'' +
                ", employeeComment='" + employeeComment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeAcceptDeclineTaskRequest that = (EmployeeAcceptDeclineTaskRequest) o;
        return Objects.equals(acceptanceStatus, that.acceptanceStatus) &&
                Objects.equals(employeeComment, that.employeeComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                acceptanceStatus,
                employeeComment
        );
    }
}
