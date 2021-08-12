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
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        LocalDateTime startOfDay = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(endDate, LocalTime.MAX);

        List<GroupTrainingDocument> groupTrainingList = groupTrainingsDAO
                .findAllByStartDateIsAfterAndEndDateIsBefore(startOfDay, endOfDay, Sort.by("startDate"));

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
