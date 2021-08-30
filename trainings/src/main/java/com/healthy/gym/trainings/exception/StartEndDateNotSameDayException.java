package com.healthy.gym.trainings.exception;

public class StartEndDateNotSameDayException extends Exception {

    public StartEndDateNotSameDayException() {
    }

    public StartEndDateNotSameDayException(String message) {
        super(message);
    }
}
