package com.healthy.gym.trainings.utils;

import java.time.Duration;
import java.time.LocalTime;

public class GroupTrainingValidator {

    private GroupTrainingValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isHallNoInvalid(int hallNo) {
        return hallNo <= 0;
    }

    public static boolean isStartTimeAfterEndTime(String startTime, String endTime) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime stop = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, stop);

        return duration.toMinutes() <= 0;
    }
}
