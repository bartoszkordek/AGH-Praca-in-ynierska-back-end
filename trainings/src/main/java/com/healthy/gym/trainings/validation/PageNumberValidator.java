package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PageNumberValidator implements ConstraintValidator<ValidPageNumber, Integer> {
    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext context) {
        return number > 0;
    }
}
