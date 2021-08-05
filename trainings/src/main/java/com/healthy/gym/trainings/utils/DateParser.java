package com.healthy.gym.trainings.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateParser {

    private DateParser() {
        throw new IllegalStateException("Utility class.");
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
