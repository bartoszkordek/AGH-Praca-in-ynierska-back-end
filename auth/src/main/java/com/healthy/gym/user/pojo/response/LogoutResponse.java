package com.healthy.gym.user.pojo.response;

import java.util.Map;
import java.util.Objects;

public class LogoutResponse extends AbstractResponse {

    private boolean success;

    public LogoutResponse() {
    }

    public LogoutResponse(String message, Map<String, String> errors, boolean success) {
        super(message, errors);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LogoutResponse that = (LogoutResponse) o;
        return success == that.success;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), success);
    }

    @Override
    public String toString() {
        return "LogoutResponse{" +
                "success=" + success +
                "} " + super.toString();
    }
}
