package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class DateFormatter {

    private static final String DEFAULT_START_DATE = "1900-01-01";
    private static final String DEFAULT_END_DATE = "2099-12-31";
    private static final String START_DATE_AFTER_END_DATE_EXCEPTION = "Start date after end date";
    private final SimpleDateFormat simpleDateFormat;

    private final Date startDate;
    private final Date endDate;

    public DateFormatter(
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException {
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.startDate = parseAndGetDate(startDate, DEFAULT_START_DATE);
        this.endDate = parseAndGetDate(endDate, DEFAULT_END_DATE);

        if (this.startDate.after(this.endDate))
            throw new StartDateAfterEndDateException(START_DATE_AFTER_END_DATE_EXCEPTION);
    }

    private Date parseAndGetDate(String date, @NotNull String defaultDate) throws ParseException {
        Optional<String> dateOptional = Optional.ofNullable(date);
        String dateToBeParsed = dateOptional.orElse(defaultDate);
        return simpleDateFormat.parse(dateToBeParsed);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getFormattedDayDateBeforeStartDate() {
        Date dayBeforeStarDate = new Date(this.startDate.getTime() - (1000 * 60 * 60 * 24));
        return simpleDateFormat.format(dayBeforeStarDate);
    }

    public String getFormattedDayDateAfterEndDate() {
        Date dayAfterEndDate = new Date(this.endDate.getTime() + (1000 * 60 * 60 * 24));
        return simpleDateFormat.format(dayAfterEndDate);
    }
}
