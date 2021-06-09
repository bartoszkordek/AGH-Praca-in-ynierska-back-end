package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface ReviewService {

    GroupTrainingsReviews createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviews,
                                                    String clientId) throws StarsOutOfRangeException;


    Page<GroupTrainingsReviews> getGroupTrainingReviews(String startDate, String endDate, Pageable pageable) throws ParseException;


}
