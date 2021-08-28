package com.healthy.gym.equipment.component;

import com.healthy.gym.equipment.exception.MultipartBodyException;

public interface MultipartFileValidator {
    <T> boolean validateBody(T body) throws MultipartBodyException;
}
