package com.healthy.gym.trainings.db;


import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingReviews extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();
}
