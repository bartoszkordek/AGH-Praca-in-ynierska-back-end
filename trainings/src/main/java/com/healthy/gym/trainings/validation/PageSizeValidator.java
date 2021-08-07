package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class PageSizeValidator implements ConstraintValidator<ValidPageSize, Integer> {

    @Override
    public boolean isValid(Integer number, ConstraintValidatorContext context) {
        Set<Integer> validSizes = Set.of(5, 10, 20, 50, 100);
        return validSizes.contains(number);
    }
}
