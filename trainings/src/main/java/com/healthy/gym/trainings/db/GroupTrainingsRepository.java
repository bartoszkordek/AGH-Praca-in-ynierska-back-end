package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.GroupTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingsRepository extends MongoRepository<GroupTrainings, String> {

    public List<GroupTrainings> findAll();
    public List<GroupTrainings> findGroupTrainingsByParticipantsContains(String clientId);
    public boolean existsById(String id);
    public boolean existsByDateAfter(String date);
    public boolean existsByIdAndDateAfter(String id, String date);
    public boolean existsByLimitGreaterThan(int participantsCount);
    public boolean existsByIdAndDateAfterAndLimitGreaterThan(String id, String date, int limit);
    public boolean existsByIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(String id, String date, String startTime, int limit);
    //public boolean existsByStart_time(int date);\\\between
    public boolean existsByStartTimeBetween(String beginning, String end);

    public GroupTrainings getFirstById(String id);
    public GroupTrainings findFirstById(String id);

    public void removeById(String id);
}
