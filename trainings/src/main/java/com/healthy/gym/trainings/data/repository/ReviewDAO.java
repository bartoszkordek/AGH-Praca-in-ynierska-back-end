package com.healthy.gym.trainings.data.repository;


import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewDAO extends MongoRepository<GroupTrainingsReviews, String> {

    boolean existsById(String id);

    boolean existsByReviewId(String reviewId);

    boolean existsByReviewIdAndAndClientId(String reviewId, String clientId);

    GroupTrainingReviewResponse getFirstByReviewId(String reviewId);

    GroupTrainingsReviews findGroupTrainingsReviewsByReviewId(String id);

    void removeByReviewId(String reviewId);

    Page<GroupTrainingReviewResponse> findByDateBetween(
            String startDate,
            String endDate,
            Pageable pageable
    );

    Page<GroupTrainingReviewResponse> findByDateBetweenAndClientId(
            String startDate,
            String endDate,
            String clientId,
            Pageable pageable
    );

    Page<GroupTrainingReviewResponse> findByDateBetweenAndTrainingTypeId(
            String startDate,
            String endDate,
            String trainingTypeId,
            Pageable pageable
    );

    Page<GroupTrainingReviewPublicResponse> getAllByDateBetweenAndTrainingTypeId(
            String startDate,
            String endDate,
            String trainingTypeId,
            Pageable pageable
    );
}
