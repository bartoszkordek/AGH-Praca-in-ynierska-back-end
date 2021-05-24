package com.healthy.gym.trainings.exception;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class RestError {
    private final String message;
    private final HttpStatus status;

    public RestError(@JsonProperty("message") String message,
                     @JsonProperty("httpStatus") HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
