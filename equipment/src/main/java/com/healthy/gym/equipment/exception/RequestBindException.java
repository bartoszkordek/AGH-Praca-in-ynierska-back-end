package com.healthy.gym.equipment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;


public class RequestBindException extends Exception {
    private final String reason;
    private final BindException exception;
    private final HttpStatus httpStatus;

    public RequestBindException(HttpStatus httpStatus, String reason, BindException exception) {
        this.reason = reason;
        this.exception = exception;
        this.httpStatus = httpStatus;
    }

    public String getReason() {
        return reason;
    }

    public BindException getException() {
        return exception;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
