package com.healthy.gym.account.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PageNumberValidator implements ConstraintValidator<ValidPageNumber, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            int number = Integer.parseInt(value);
            return number >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}
