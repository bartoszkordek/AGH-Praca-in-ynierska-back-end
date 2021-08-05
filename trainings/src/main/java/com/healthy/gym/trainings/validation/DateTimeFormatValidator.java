package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatValidator implements ConstraintValidator<ValidDateTimeFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
