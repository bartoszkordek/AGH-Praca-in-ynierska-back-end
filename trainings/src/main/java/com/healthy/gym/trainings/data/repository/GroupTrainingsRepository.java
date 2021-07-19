package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingsRepository extends MongoRepository<GroupTrainings, String> {

    public List<GroupTrainings> findAll();
    public List<GroupTrainings> findByDateBetween(String startDate, String endDate);
    public List<GroupTrainings> findGroupTrainingsByParticipantsContains(String clientId);
    public List<GroupTrainings> findAllByTrainingTypeIdAndDateBetween(String trainingTypeId, String startDate, String endDate);
    public boolean existsByTrainingId(String trainingId);
    public boolean existsByTrainingTypeId(String trainingTypeId);
    public boolean existsByDateAfter(String date);
    public boolean existsByTrainingIdAndDateAfter(String trainingId, String date);
    public boolean existsByLimitGreaterThan(int participantsCount);
    public boolean existsByTrainingIdAndDateAfterAndLimitGreaterThan(String trainingId, String date, int limit);
    public boolean existsByTrainingIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(String trainingId, String date, String startTime, int limit);
    //public boolean existsByStart_time(int date);\\\between
    public boolean existsByStartTimeBetween(String beginning, String end);

    public GroupTrainings getFirstByTrainingId(String trainingId);
    public GroupTrainings findFirstByTrainingId(String trainingId);

    public void removeByTrainingId(String trainingId);
}
