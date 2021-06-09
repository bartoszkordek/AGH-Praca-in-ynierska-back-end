package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingService {

    List<GroupTrainings> getGroupTrainings();

    List<GroupTrainingPublicResponse> getPublicGroupTrainings()
            throws InvalidHourException, InvalidDateException;

    GroupTrainings getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException;

    List<GroupTrainings> getMyAllTrainings(String clientId);

    List<String> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException;

    void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException;

    void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    GroupTrainings createGroupTraining(GroupTrainingRequest groupTrainingModel)
            throws TrainingCreationException, ParseException, InvalidHourException;

    GroupTrainings removeGroupTraining(String trainingId)
            throws TrainingRemovalException, EmailSendingException;

    GroupTrainings updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException, EmailSendingException, InvalidHourException, ParseException;

    List<GroupTrainingsReviews> getGroupTrainingReviews();

    GroupTrainingsReviews getGroupTrainingReviewById(String reviewId)
            throws NotExistingGroupTrainingReviewException;

    GroupTrainingsReviews createGroupTrainingReview(
            GroupTrainingReviewRequest groupTrainingsReviewsModel,
            String clientId
    ) throws StarsOutOfRangeException;

    GroupTrainingsReviews removeGroupTrainingReview(String reviewId, String clientId)
            throws NotAuthorizedClientException, NotExistingGroupTrainingReviewException;

    GroupTrainingsReviews updateGroupTrainingReview(
            GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
            String reviewId,
            String clientId
    ) throws NotAuthorizedClientException, StarsOutOfRangeException, NotExistingGroupTrainingReviewException;
}
