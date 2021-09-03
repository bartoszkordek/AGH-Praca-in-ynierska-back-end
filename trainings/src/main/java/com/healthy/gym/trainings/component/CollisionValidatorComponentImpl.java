package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.utils.CollisionValidator;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class CollisionValidatorComponentImpl implements CollisionValidatorComponent {
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final IndividualTrainingRepository individualTrainingRepository;

    public CollisionValidatorComponentImpl(
            GroupTrainingsDAO groupTrainingsDAO,
            IndividualTrainingRepository individualTrainingRepository
    ) {
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.individualTrainingRepository = individualTrainingRepository;
    }

    @Override
    public CollisionValidator getCollisionValidator(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDateTime startOfDay = getStartOfDate(startDateTime);
        LocalDateTime endOfDay = getEndOfDate(endDateTime);

        List<GroupTrainingDocument> groupTrainingList = groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBefore(startOfDay, endOfDay, Sort.by("startDate"));

        List<IndividualTrainingDocument> individualTrainingList = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndCancelledIsFalseAndRejectedIsFalse(
                        startOfDay, endOfDay, Sort.by("startDateTime")
                );

        return new CollisionValidator(
                groupTrainingList,
                individualTrainingList,
                startDateTime,
                endDateTime
        );
    }

    private LocalDateTime getStartOfDate(LocalDateTime startDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        return LocalDateTime.of(startDate, LocalTime.MIN);
    }

    private LocalDateTime getEndOfDate(LocalDateTime endDateTime) {
        LocalDate endDate = endDateTime.toLocalDate();
        return LocalDateTime.of(endDate, LocalTime.MAX);
    }

    @Override
    public CollisionValidator getCollisionValidator(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String trainingId
    ) {
        LocalDateTime startOfDay = getStartOfDate(startDateTime);
        LocalDateTime endOfDay = getEndOfDate(endDateTime);

        List<GroupTrainingDocument> groupTrainingList = groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBeforeAndGroupTrainingIdIsNot(
                        startOfDay,
                        endOfDay,
                        trainingId,
                        Sort.by("startDate")
                );

        List<IndividualTrainingDocument> individualTrainingList = individualTrainingRepository
                .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startOfDay, endOfDay, Sort.by("startDateTime"));

        return new CollisionValidator(
                groupTrainingList,
                individualTrainingList,
                startDateTime,
                endDateTime
        );
    }
}
