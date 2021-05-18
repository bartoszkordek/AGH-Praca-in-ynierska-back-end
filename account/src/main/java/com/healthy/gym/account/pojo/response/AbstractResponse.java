package com.healthy.gym.account.pojo.response;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractResponse implements Serializable {

    private String message;
    private Map<String,String> errors;

    protected AbstractResponse() {
    }

    protected AbstractResponse(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResponse that = (AbstractResponse) o;
        return Objects.equals(message, that.message) && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, errors);
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "message='" + message + '\'' +
                ", errors=" + errors +
                '}';
    }
}
