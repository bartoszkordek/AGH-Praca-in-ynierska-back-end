package com.healthy.gym.trainings.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateValidator {

    private static final String DATE_PATTERN = "^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$";

    private DateValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean validate(final String time) {
        Pattern pattern = Pattern.compile(DATE_PATTERN);

        Matcher matcher = pattern.matcher(time);
        return matcher.matches();

    }
}
