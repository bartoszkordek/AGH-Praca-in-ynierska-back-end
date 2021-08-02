package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.UserResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingService {

    List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException;

    List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException;

    GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException, InvalidHourException, InvalidDateException;

    List<GroupTrainingResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
            throws NotExistingGroupTrainingException, InvalidDateException, InvalidHourException,
            StartDateAfterEndDateException, ParseException, TrainingTypeNotFoundException;

    List<GroupTrainingPublicResponse> getGroupTrainingsPublicByType(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws TrainingTypeNotFoundException,
            NotExistingGroupTrainingException, InvalidDateException, InvalidHourException,
            StartDateAfterEndDateException, ParseException;

    List<UserResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException;
}
