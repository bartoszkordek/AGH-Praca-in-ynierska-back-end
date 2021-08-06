package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingResponseOld;
import com.healthy.gym.trainings.model.response.UserResponse;

import java.util.List;

public interface EmployeeGroupTrainingService {

    List<UserResponse> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException;

    GroupTrainingResponseOld getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException,
            InvalidHourException,
            InvalidDateException;
}
