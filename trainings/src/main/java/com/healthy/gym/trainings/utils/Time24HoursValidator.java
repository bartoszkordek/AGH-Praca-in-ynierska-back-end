package com.healthy.gym.trainings.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time24HoursValidator {

    private static final String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    private Time24HoursValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean validate(final String time) {
        Pattern pattern = Pattern.compile(TIME24HOURS_PATTERN);

        Matcher matcher = pattern.matcher(time);
        return matcher.matches();

    }
}
