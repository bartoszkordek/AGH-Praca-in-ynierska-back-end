package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.healthy.gym.trainings.utils.DateParser.parseDate;

public class StartEndDateValidator {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public StartEndDateValidator(String startDate, String endDate) throws StartDateAfterEndDateException {
        this.startDate = parseDate(startDate);
        this.endDate = parseDate(endDate);
        if (this.startDate.isAfter(this.endDate)) throw new StartDateAfterEndDateException();
    }

    public LocalDateTime getBeginningOfStartDate() {
        return LocalDateTime.of(startDate, LocalTime.MIN);
    }

    public LocalDateTime getEndOfEndDate() {
        return LocalDateTime.of(endDate, LocalTime.MAX);
    }
}
