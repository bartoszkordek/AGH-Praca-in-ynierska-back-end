package com.healthy.gym.user.pojo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Objects;

public class CreateUserResponse {

    @JsonProperty("id")
    private String userId;
    private boolean success;
    private String message;
    private Map<String, String> errors;

    public CreateUserResponse() {
    }

    public CreateUserResponse(
            boolean success,
            String message,
            Map<String, String> errors,
            String userId
    ) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.userId = userId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUserResponse that = (CreateUserResponse) o;
        return success == that.success
                && Objects.equals(userId, that.userId)
                && Objects.equals(message, that.message)
                && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, success, message, errors);
    }
}
