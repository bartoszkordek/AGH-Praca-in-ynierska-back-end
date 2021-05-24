package com.healthy.gym.account.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailValidatorNullable implements ConstraintValidator<ValidEmail, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        var validator = org.apache.commons.validator.routines.EmailValidator.getInstance();
        return validator.isValid(value);
    }
}