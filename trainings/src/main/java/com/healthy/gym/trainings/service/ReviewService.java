package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface ReviewService {

    GroupTrainingReviewResponse createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviews,
                                                    String clientId) throws StarsOutOfRangeException;


    Page<GroupTrainingReviewResponse> getAllReviews(String startDate, String endDate, Pageable pageable) throws ParseException, StartDateAfterEndDateException;
    Page<GroupTrainingReviewResponse> getAllReviewsByUserId(String startDate, String endDate, String userId, Pageable pageable) throws ParseException, StartDateAfterEndDateException;
    Page<GroupTrainingReviewResponse> getAllReviewsByTrainingTypeId(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException;

}
