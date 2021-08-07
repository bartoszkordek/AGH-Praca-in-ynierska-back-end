package com.healthy.gym.gympass.exception;

public class StartDateAfterEndDateException extends Exception {

    public StartDateAfterEndDateException() {}

    public StartDateAfterEndDateException(String message) {
        super(message);
    }

}
