package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.shared.GroupTrainingEnrollmentDTO;
import com.healthy.gym.trainings.model.response.GroupTrainingsPublicResponse;

import java.util.List;

public interface UserGroupTrainingService {

    GroupTrainingsPublicResponse getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException, UserNotFoundException;

    GroupTrainingEnrollmentDTO enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException, NotExistingGroupTrainingException, UserNotFoundException, InvalidHourException, InvalidDateException;

    void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException;

    void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException;
}
