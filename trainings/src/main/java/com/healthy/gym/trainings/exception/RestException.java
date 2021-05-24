package com.healthy.gym.trainings.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RestException extends Exception {
    private final HttpStatus status;

    public RestException(final String message,
                         final HttpStatus status,
                         final Throwable cause) {
        super(message, cause);
        this.status = status;
        throw new ResponseStatusException(
                status, message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
