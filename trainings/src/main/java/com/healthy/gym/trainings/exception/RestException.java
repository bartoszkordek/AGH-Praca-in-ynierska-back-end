package com.healthy.gym.trainings.exception;

import org.springframework.http.HttpStatus;

public class RestException extends Exception {
    private final HttpStatus status;

    public RestException(final String message,
                         final HttpStatus status,
                         final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
