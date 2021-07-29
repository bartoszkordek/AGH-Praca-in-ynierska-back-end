package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidUserIdException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface ReviewService {

    GroupTrainingReviewResponse createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviews,
                                                    String clientId) throws StarsOutOfRangeException, TrainingTypeNotFoundException;


    GroupTrainingReviewResponse getReviewByReviewId(String reviewId) throws NotExistingGroupTrainingReviewException;
    Page<GroupTrainingReviewResponse> getAllReviews(String startDate, String endDate, Pageable pageable) throws ParseException, StartDateAfterEndDateException;
    Page<GroupTrainingReviewResponse> getAllReviewsByUserId(String startDate, String endDate, String userId, Pageable pageable) throws ParseException, StartDateAfterEndDateException, InvalidUserIdException;
    Page<GroupTrainingReviewResponse> getAllReviewsByTrainingTypeId(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException, TrainingTypeNotFoundException;
    Page<GroupTrainingReviewPublicResponse> getAllReviewsByTrainingTypeIdPublic(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException, TrainingTypeNotFoundException;

    GroupTrainingReviewResponse updateGroupTrainingReviewByReviewId(GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel, String reviewId, String clientId) throws NotExistingGroupTrainingReviewException, NotAuthorizedClientException, StarsOutOfRangeException;
    GroupTrainingReviewResponse removeGroupTrainingReviewByReviewId(String reviewId, String clientId) throws NotExistingGroupTrainingReviewException, NotAuthorizedClientException;
}
