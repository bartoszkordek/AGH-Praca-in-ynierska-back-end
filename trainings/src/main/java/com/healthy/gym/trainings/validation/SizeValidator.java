package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class SizeValidator implements ConstraintValidator<ValidSize, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            int number = Integer.parseInt(value);
            Set<Integer> validSizes = Set.of(5, 10, 20, 50, 100);
            return validSizes.contains(number);
        } catch (Exception e) {
            return false;
        }
    }
}
