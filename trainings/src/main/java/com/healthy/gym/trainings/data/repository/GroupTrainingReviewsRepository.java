package com.healthy.gym.trainings.data.repository;


import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupTrainingReviewsRepository extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();

    public boolean existsById(String id);
    public boolean existsByIdAndAndClientId(String reviewId, String clientId);

    public GroupTrainingsReviews findFirstBy(String id);
    public GroupTrainingsReviews getFirstBy(String id);
    public GroupTrainingsReviews findGroupTrainingsReviewsById(String id);

    public void removeById(String id);
}
