package com.healthy.gym.account.exception;

public class OldPasswordDoesNotMatchException extends Exception {

    public OldPasswordDoesNotMatchException() {
    }

    public OldPasswordDoesNotMatchException(String message) {
        super(message);
    }
}
