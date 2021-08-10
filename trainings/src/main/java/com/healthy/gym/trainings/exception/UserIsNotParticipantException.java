package com.healthy.gym.trainings.exception;

public class UserIsNotParticipantException extends Exception {
    public UserIsNotParticipantException() {
    }

    public UserIsNotParticipantException(String message) {
        super(message);
    }
}
