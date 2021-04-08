package com.healthy.gym.user.pojo.response;

import java.util.Map;

public class CreateUserResponse {
    private boolean success;
    private String message;
    private Map<String, String> errors;

    public CreateUserResponse() {
    }

    public CreateUserResponse(boolean success, String message, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.errors = errors;
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
}
