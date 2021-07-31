package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingsRepository extends MongoRepository<GroupTrainings, String> {

    List<GroupTrainings> findByDateBetween(String startDate, String endDate);

    List<GroupTrainings> findGroupTrainingsByParticipantsContains(String clientId);

    List<GroupTrainings> findAllByTrainingTypeIdAndDateBetween(String trainingTypeId, String startDate, String endDate);

    boolean existsByTrainingId(String trainingId);

    boolean existsByTrainingTypeId(String trainingTypeId);

    boolean existsByTrainingIdAndDateAfterAndLimitGreaterThan(String trainingId, String date, int limit);

    boolean existsByTrainingIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(String trainingId, String date, String startTime, int limit);

    GroupTrainings getFirstByTrainingId(String trainingId);

    GroupTrainings findFirstByTrainingId(String trainingId);

    void removeByTrainingId(String trainingId);
}
