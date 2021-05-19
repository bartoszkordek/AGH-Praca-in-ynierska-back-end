package com.healthy.gym.account.pojo.response;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractResponse implements Serializable {

    private String message;

    protected AbstractResponse() {
    }

    protected AbstractResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractResponse that = (AbstractResponse) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
