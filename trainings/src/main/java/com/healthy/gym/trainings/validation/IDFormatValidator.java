package com.healthy.gym.trainings.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDFormatValidator implements ConstraintValidator<ValidIDFormat, String> {

    private static final String ID_VALUE_PATTERN =
            "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b";

    @Override
    public boolean isValid(String valueId, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(ID_VALUE_PATTERN);
        Matcher matcher = pattern.matcher(valueId);
        return matcher.matches();
    }
}
