package com.healthy.gym.trainings.data.repository;


import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.InvalidUserIdException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.text.ParseException;
import java.util.List;

public interface ReviewDAO extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();

    public boolean existsById(String id);
    public boolean existsByIdAndAndClientId(String reviewId, String clientId);

    public GroupTrainingsReviews findFirstBy(String id);
    public GroupTrainingsReviews getFirstBy(String id);
    public GroupTrainingsReviews findGroupTrainingsReviewsById(String id);

    public void removeById(String id);

    Page<GroupTrainingReviewResponse> findByDateBetween(String startDate, String endDate, Pageable pageable);
    Page<GroupTrainingReviewResponse> findByDateBetweenAndClientId(String startDate, String endDate, String clientId, Pageable pageable);

    //TBC if TrainingName == Training Type ID
    Page<GroupTrainingReviewResponse> findByDateBetweenAndTrainingName(String startDate, String endDate, String trainingTypeId, Pageable pageable);
}
