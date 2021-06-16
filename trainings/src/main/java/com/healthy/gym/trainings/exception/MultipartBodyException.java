package com.healthy.gym.trainings.exception;

import java.util.Map;

public class MultipartBodyException extends Exception {
    private Map<String, String> errorMap;

    public MultipartBodyException(Map<String, String> errorMap) {
        this.errorMap = errorMap;
    }

    public MultipartBodyException(Map<String, String> errorMap, String message) {
        super(message);
        this.errorMap = errorMap;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }
}
