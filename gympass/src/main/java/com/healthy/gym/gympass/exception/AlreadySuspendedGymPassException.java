package com.healthy.gym.gympass.exception;

public class AlreadySuspendedGymPassException extends Exception {

    public AlreadySuspendedGymPassException() {}

    public AlreadySuspendedGymPassException(String message) {
        super(message);
    }

}