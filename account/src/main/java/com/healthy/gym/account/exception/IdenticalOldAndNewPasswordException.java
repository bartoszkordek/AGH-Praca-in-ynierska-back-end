package com.healthy.gym.account.exception;

public class IdenticalOldAndNewPasswordException extends Exception {
    public IdenticalOldAndNewPasswordException() {
    }

    public IdenticalOldAndNewPasswordException(String message) {
        super(message);
    }
}
