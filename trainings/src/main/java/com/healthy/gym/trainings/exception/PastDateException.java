package com.healthy.gym.trainings.exception;

public class PastDateException extends Exception {
    public PastDateException() {
    }

    public PastDateException(String message) {
        super(message);
    }
}
