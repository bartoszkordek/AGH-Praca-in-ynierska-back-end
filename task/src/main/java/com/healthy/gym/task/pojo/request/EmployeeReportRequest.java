package com.healthy.gym.task.pojo.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class EmployeeReportRequest {

    @NotNull(message = "{field.required}")
    @Size(min = 2, max = 500, message = "{field.result.failure}")
    private String result;

    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "EmployeeReportRequest{" +
                "result='" + result + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeReportRequest that = (EmployeeReportRequest) o;
        return Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result);
    }
}
