package com.healthy.gym.user.exceptions.token;

public class ExpiredTokenException extends Exception {

    public ExpiredTokenException() {
    }

    public ExpiredTokenException(String message) {
        super(message);
    }
}
