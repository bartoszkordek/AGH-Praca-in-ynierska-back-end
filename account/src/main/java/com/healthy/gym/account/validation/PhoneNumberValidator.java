package com.healthy.gym.account.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_NUMBER_PATTER =
            "^(\\+\\d{2})?(|[ -])(\\d{3}(\\2\\d{3}){2})$|^(\\d{3}([ -]))\\d{3}\\6\\d{3}$";

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTER);

        if (phoneNumber == null || phoneNumber.trim().length() == 0) return true;

        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
