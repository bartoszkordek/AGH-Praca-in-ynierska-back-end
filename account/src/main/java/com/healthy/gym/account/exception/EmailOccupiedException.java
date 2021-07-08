package com.healthy.gym.account.exception;

public class EmailOccupiedException extends Exception {
    public EmailOccupiedException() {
    }

    public EmailOccupiedException(String message) {
        super(message);
    }
}
