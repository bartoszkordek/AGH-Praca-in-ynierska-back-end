package com.healthy.gym.trainings.data.repository;


import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewDAO extends MongoRepository<GroupTrainingsReviews, String> {

    public List<GroupTrainingsReviews> findAll();

    public boolean existsById(String id);
    public boolean existsByIdAndAndClientId(String reviewId, String clientId);

    public GroupTrainingsReviews findFirstBy(String id);
    public GroupTrainingsReviews getFirstBy(String id);
    public GroupTrainingsReviews findGroupTrainingsReviewsById(String id);

    public void removeById(String id);

    Page<GroupTrainingsReviews> findAllByDateAfterAndDateBefore(String startDate, String endDate, Pageable pageable);


}
