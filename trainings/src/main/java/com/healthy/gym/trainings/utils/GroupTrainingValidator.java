package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.model.request.GroupTrainingRequestOld;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class GroupTrainingValidator {

    private GroupTrainingValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isExistRequiredDataForGroupTraining(GroupTrainingRequestOld groupTrainingModel) {
        String trainingName = groupTrainingModel.getTrainingTypeId();
        List<String> trainers = groupTrainingModel.getTrainers();
        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();

        return !trainingName.isEmpty()
                && !trainers.isEmpty()
                && !date.isEmpty()
                && !startTime.isEmpty()
                && !endTime.isEmpty();
    }

    public static boolean isHallNoInvalid(int hallNo) {
        return hallNo <= 0;
    }

    public static boolean isLimitInvalid(int limit) {
        return limit <= 0;
    }

    public static boolean isTrainingRetroDate(String date) throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDateParsed = sdfDate.parse(date);
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        Date todayDateParsed = sdfDate.parse(todayDateFormatted);

        return requestDateParsed.before(todayDateParsed);
    }

    public static boolean isStartTimeAfterEndTime(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime stop = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, stop);

        return duration.toMinutes() <= 0;
    }
}
