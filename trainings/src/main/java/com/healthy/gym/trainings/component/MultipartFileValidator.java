package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.exception.MultipartBodyException;

import java.util.Map;

public interface MultipartFileValidator {
    <T> boolean validateBody(T body) throws MultipartBodyException;
}
