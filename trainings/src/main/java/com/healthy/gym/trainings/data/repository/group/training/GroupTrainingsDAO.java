package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupTrainingsDAO extends MongoRepository<GroupTrainingDocument, String> {

    GroupTrainingDocument findFirstByGroupTrainingId(String trainingId);

    List<GroupTrainingDocument> findAllByStartDateIsAfterAndEndDateIsBefore(
            LocalDateTime startDateTime, LocalDateTime endDateTime, Sort sort
    );

    List<GroupTrainingDocument> findAllByStartDateIsAfterAndEndDateIsBeforeAndGroupTrainingIdIsNot(
            LocalDateTime startDateTime, LocalDateTime endDateTime, String trainingId, Sort sort
    );
}
