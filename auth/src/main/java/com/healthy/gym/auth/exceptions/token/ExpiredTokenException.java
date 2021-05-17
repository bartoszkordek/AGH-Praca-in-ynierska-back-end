package com.healthy.gym.auth.exceptions.token;

public class ExpiredTokenException extends Exception {

    public ExpiredTokenException() {
    }

    public ExpiredTokenException(String message) {
        super(message);
    }
}
