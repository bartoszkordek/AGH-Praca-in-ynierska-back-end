package com.healthy.gym.task.exception;

public class TaskDeclinedByEmployeeException extends Exception {

    public TaskDeclinedByEmployeeException() {}

    public TaskDeclinedByEmployeeException(String message) {
        super(message);
    }
}
