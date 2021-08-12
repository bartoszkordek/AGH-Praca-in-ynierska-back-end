package com.healthy.gym.trainings.utils;

import javax.validation.constraints.NotNull;

public class CollisionDetection {

    private CollisionDetection() {
        throw new IllegalStateException("Utility class.");
    }

    public static boolean overlapsWithAny(@NotNull long[] checkedDates, @NotNull long[][] sortedRangeOfDates) {
        if (sortedRangeOfDates.length == 0) return false;

        long startDateTime = checkedDates[0];
        int[] indexPointers = modifiedBinarySearchGetPointersToNearestNumbers(startDateTime, sortedRangeOfDates);

        boolean overlapsWithFirstInterval = overlaps(checkedDates, sortedRangeOfDates, indexPointers[0]);
        boolean overlapsWithSecondInterval = overlaps(checkedDates, sortedRangeOfDates, indexPointers[1]);

        return overlapsWithFirstInterval || overlapsWithSecondInterval;
    }

    private static boolean overlaps(long[] checkedDates, long[][] sortedRangeOfDates, int indexPointer) {
        boolean overlapsWithInterval = false;
        if (indexPointer != -1) {
            long[] interval = sortedRangeOfDates[indexPointer];
            overlapsWithInterval = overlaps(checkedDates, interval);
        }
        return overlapsWithInterval;
    }


    public static boolean overlaps(long[] checkedInterval, long[] comparativeInterval) {
        long checkedStartDateTime = checkedInterval[0];
        long checkedEndDateTime = checkedInterval[1];

        long comparativeStartDateTime = comparativeInterval[0];
        long comparativeEndDateTime = comparativeInterval[1];

        return !(checkedEndDateTime <= comparativeStartDateTime || checkedStartDateTime >= comparativeEndDateTime);
    }

    public static int[] modifiedBinarySearchGetPointersToNearestNumbers(
            long number,
            @NotNull long[][] sortedRangeOfDates
    ) {
        int leftPointer = 0;
        int rightPointer = sortedRangeOfDates.length - 1;
        int middlePointer = 0;
        boolean lastStepWasInRightDirection = true;

        while (leftPointer <= rightPointer) {
            middlePointer = (leftPointer + rightPointer) / 2;
            long searchedNumber = sortedRangeOfDates[middlePointer][0];

            if (searchedNumber == number) {
                lastStepWasInRightDirection = true;
                break;
            }

            if (number < searchedNumber) {
                rightPointer = middlePointer - 1;
                lastStepWasInRightDirection = false;
            } else {
                leftPointer = middlePointer + 1;
                lastStepWasInRightDirection = true;
            }
        }

        if (lastStepWasInRightDirection) {
            int secondPointer = middlePointer == (sortedRangeOfDates.length - 1) ? -1 : middlePointer + 1;
            return new int[]{middlePointer, secondPointer};
        }

        return new int[]{middlePointer - 1, middlePointer};
    }
}
