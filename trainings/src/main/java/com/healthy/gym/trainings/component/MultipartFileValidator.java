package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.exception.MultipartBodyException;

public interface MultipartFileValidator {
    <T> boolean validateBody(T body) throws MultipartBodyException;
}
