package com.healthy.gym.trainings.data.repository;


import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewDAO extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();

    public boolean existsById(String id);
    public boolean existsByReviewId(String reviewId);
    public boolean existsByReviewIdAndAndClientId(String reviewId, String clientId);

    public GroupTrainingReviewResponse getFirstByReviewId(String reviewId);
    public GroupTrainingsReviews getFirstBy(String id);
    public GroupTrainingsReviews findGroupTrainingsReviewsById(String id);

    public void removeById(String id);

    Page<GroupTrainingReviewResponse> findByDateBetween(String startDate, String endDate, Pageable pageable);
    Page<GroupTrainingReviewResponse> findByDateBetweenAndClientId(String startDate, String endDate, String clientId, Pageable pageable);

    //TBC if TrainingName == Training Type ID
    Page<GroupTrainingReviewResponse> findByDateBetweenAndTrainingName(String startDate, String endDate, String trainingTypeId, Pageable pageable);
    Page<GroupTrainingReviewPublicResponse> getAllByDateBetweenAndTrainingName(String startDate, String endDate, String trainingTypeId, Pageable pageable);
}
