package com.healthy.gym.task.exception;

public class StartDateAfterEndDateException extends Exception {

    public StartDateAfterEndDateException() {}

    public StartDateAfterEndDateException(String message) {
        super(message);
    }

}
