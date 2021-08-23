package com.healthy.gym.account.exception;

public class NoUserFound extends Exception {

    public NoUserFound() {
    }

    public NoUserFound(String message) {
        super(message);
    }
}
