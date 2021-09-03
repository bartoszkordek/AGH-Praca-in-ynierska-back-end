package com.healthy.gym.gympass.exception;

public class PastDateException extends Exception {
    public PastDateException() {
    }

    public PastDateException(String message) {
        super(message);
    }
}
