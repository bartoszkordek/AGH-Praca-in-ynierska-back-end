package com.healthy.gym.user.exceptions.token;

public class InvalidTokenException extends Exception {
    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
