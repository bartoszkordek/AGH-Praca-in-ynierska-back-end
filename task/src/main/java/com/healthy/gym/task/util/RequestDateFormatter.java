package com.healthy.gym.task.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RequestDateFormatter {

    private String startDate;
    private String endDate;

    public RequestDateFormatter(){ }

    public RequestDateFormatter(
            String startDate,
            String endDate
    ){
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public LocalDate formatStartDate(String startDate){
        return LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE).minusDays(1);
    }

    public LocalDate formatEndDate(String endDate){
        return LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE).plusDays(1);
    }
}
