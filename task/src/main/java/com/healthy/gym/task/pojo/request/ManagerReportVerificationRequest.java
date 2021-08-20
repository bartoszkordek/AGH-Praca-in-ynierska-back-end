package com.healthy.gym.task.pojo.request;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class ManagerReportVerificationRequest {

    @NotNull(message = "{field.required}")
    private String approvalStatus;

    @NotNull(message = "{field.required}")
    private int mark;

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public int getMark() {
        return mark;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "ManagerReportVerificationRequest{" +
                "approvalStatus='" + approvalStatus + '\'' +
                ", mark=" + mark +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerReportVerificationRequest that = (ManagerReportVerificationRequest) o;
        return mark == that.mark &&
                Objects.equals(approvalStatus, that.approvalStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                approvalStatus,
                mark
        );
    }
}
