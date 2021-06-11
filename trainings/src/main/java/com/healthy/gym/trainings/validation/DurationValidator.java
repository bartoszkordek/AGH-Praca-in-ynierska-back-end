package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DurationValidator implements ConstraintValidator<ValidDurationFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalTime.parse(value, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }
}
