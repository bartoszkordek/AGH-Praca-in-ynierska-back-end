package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class DateFormatter {

    private static final LocalDateTime DEFAULT_START_DATE = LocalDateTime.of(
            1900, 1, 1,0,0);
    private static final LocalDateTime DEFAULT_END_DATE = LocalDateTime.of(
            2099, 12, 31,23,0);
    private static final String START_DATE_AFTER_END_DATE_EXCEPTION = "Start date after end date";

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public DateFormatter(
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException {
        this.startDate = parseAndGetDate(
                LocalDateTime.parse(
                        startDate.concat("T00:00:00"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                DEFAULT_START_DATE);
        this.endDate = parseAndGetDate(
                LocalDateTime.parse(
                        endDate.concat("T00:00:00"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                DEFAULT_END_DATE);

        if (this.startDate.isAfter(this.endDate))
            throw new StartDateAfterEndDateException(START_DATE_AFTER_END_DATE_EXCEPTION);
    }

    private LocalDateTime parseAndGetDate(LocalDateTime date, @NotNull LocalDateTime defaultDate) throws ParseException {
        Optional<LocalDateTime> dateOptional = Optional.ofNullable(date);
        LocalDateTime dateToBeParsed = dateOptional.orElse(defaultDate);
        return dateToBeParsed;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public LocalDateTime getDayDateBeforeStartDate() {
        return startDate.minusDays(1);
    }

    public LocalDateTime getDayDateAfterEndDate() {
        return endDate.plusDays(2);
    }
}
