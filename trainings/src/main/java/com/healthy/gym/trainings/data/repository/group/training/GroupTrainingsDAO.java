package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupTrainingsDAO extends MongoRepository<GroupTrainingDocument, String> {

    GroupTrainingDocument findFirstByGroupTrainingId(String trainingId);

    List<GroupTrainingDocument> findByStartDateAfterAndEndDateBefore(LocalDateTime startDate,
                                                                     LocalDateTime endDate);

    List<GroupTrainingDocument> findByTrainingAndStartDateAfterAndEndDateBefore(TrainingTypeDocument training,
                                                                                LocalDateTime startDate,
                                                                                LocalDateTime endDate);
}
