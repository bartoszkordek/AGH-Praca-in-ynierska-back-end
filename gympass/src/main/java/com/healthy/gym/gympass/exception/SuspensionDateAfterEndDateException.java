package com.healthy.gym.gympass.exception;

public class SuspensionDateAfterEndDateException extends Exception {

    public SuspensionDateAfterEndDateException() {}

    public SuspensionDateAfterEndDateException(String message) {
        super(message);
    }

}
