package com.healthy.gym.trainings.db;


import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingReviewsRepository extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();

    public boolean existsById(String id);

    public GroupTrainingsReviews findFirstBy(String id);

    public void removeById(String id);
}
