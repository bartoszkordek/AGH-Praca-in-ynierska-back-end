package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupTrainingsDAO extends MongoRepository<GroupTrainingDocument, String> {

    //GroupTrainingDocument getFirstById(String trainingId);
    GroupTrainingDocument findFirstByGroupTrainingId(String trainingId);

    List<GroupTrainingDocument> findByStartDateAfterAndEndDateBefore(LocalDateTime startDate,
                                                                     LocalDateTime endDate);

    List<GroupTrainingDocument> findByTrainingAndStartDateAfterAndEndDateBefore(TrainingTypeDocument training,
                                                                                LocalDateTime startDate,
                                                                                LocalDateTime endDate);
    List<GroupTrainingDocument> findByBasicListContains(UserDocument user);
}
