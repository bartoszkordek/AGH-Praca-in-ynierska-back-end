package com.healthy.gym.trainings.exception;

public class NotAuthorizedClientException extends Exception {

    public NotAuthorizedClientException() {
    }

    public NotAuthorizedClientException(String message) {
        super(message);
    }
}
