package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollisionValidator {

    private final List<GroupTrainingDocument> groupTrainingDocumentList;
    private final List<IndividualTrainingDocument> individualTrainingDocumentList;
    private final long[] datesToCheck;


    public CollisionValidator(
            List<GroupTrainingDocument> groupTrainingDocumentListSortedByStartDate,
            List<IndividualTrainingDocument> individualTrainingDocumentListSortedByStartDateTime,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {
        this.groupTrainingDocumentList = groupTrainingDocumentListSortedByStartDate;
        this.individualTrainingDocumentList = individualTrainingDocumentListSortedByStartDateTime;
        this.datesToCheck = new long[2];
        this.datesToCheck[0] = startDateTime.toEpochSecond(ZoneOffset.UTC);
        this.datesToCheck[1] = endDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    private static boolean containsAny(List<UserDocument> trainers, List<UserDocument> trainersToCheck) {
        Set<UserDocument> trainersSet = new HashSet<>(trainers);
        for (UserDocument trainer : trainersToCheck) {
            if (trainersSet.contains(trainer)) return true;
        }
        return false;
    }

    public boolean isLocationOccupied(LocationDocument location) {
        long[][] groupTrainingDates = groupTrainingDocumentList
                .stream()
                .filter(groupTrainingDocument -> location.equals(groupTrainingDocument.getLocation()))
                .map(GroupTrainingMapper::mapGroupTrainingToPairOfStartAndEndDates)
                .collect(Collectors.toList())
                .toArray(long[][]::new);

        boolean isOverlappingWithAnyGroupTraining = CollisionDetection.overlapsWithAny(datesToCheck, groupTrainingDates);
        if (isOverlappingWithAnyGroupTraining) return true;

        long[][] individualTrainingDates = individualTrainingDocumentList
                .stream()
                .filter(individualTrainingDocument -> location.equals(individualTrainingDocument.getLocation()))
                .map(IndividualTrainingMapper::mapIndividualTrainingToPairOfStartAndEndDate)
                .collect(Collectors.toList())
                .toArray(long[][]::new);

        return CollisionDetection.overlapsWithAny(datesToCheck, individualTrainingDates);
    }

    public boolean isTrainerOccupied(List<UserDocument> trainers) {
        long[][] groupTrainingDates = groupTrainingDocumentList
                .stream()
                .filter(training -> containsAny(training.getTrainers(), trainers))
                .map(GroupTrainingMapper::mapGroupTrainingToPairOfStartAndEndDates)
                .collect(Collectors.toList())
                .toArray(long[][]::new);

        boolean isOverlappingWithAnyGroupTraining = CollisionDetection.overlapsWithAny(datesToCheck, groupTrainingDates);
        if (isOverlappingWithAnyGroupTraining) return true;

        long[][] individualTrainingDates = individualTrainingDocumentList
                .stream()
                .filter(training -> containsAny(training.getTrainers(), trainers))
                .map(IndividualTrainingMapper::mapIndividualTrainingToPairOfStartAndEndDate)
                .collect(Collectors.toList())
                .toArray(long[][]::new);

        return CollisionDetection.overlapsWithAny(datesToCheck, individualTrainingDates);
    }
}
