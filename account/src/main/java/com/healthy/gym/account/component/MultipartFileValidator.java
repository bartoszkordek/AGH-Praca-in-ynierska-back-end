package com.healthy.gym.account.component;

import com.healthy.gym.account.exception.MultipartBodyException;

public interface MultipartFileValidator {
    <T> boolean validateBody(T body) throws MultipartBodyException;
}
