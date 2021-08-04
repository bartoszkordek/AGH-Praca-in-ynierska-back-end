package com.healthy.gym.trainings.exception;

public class UserAlreadyEnrolledToTrainingException extends Exception {

    public UserAlreadyEnrolledToTrainingException() {
    }

    public UserAlreadyEnrolledToTrainingException(String message) {
        super(message);
    }
}
