package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;

import java.util.List;

public interface UserGroupTrainingService {

    List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException;

    void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException;

    void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;
}
