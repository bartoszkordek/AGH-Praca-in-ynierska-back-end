package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingParticipantsResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingsPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingsResponse;

import java.text.ParseException;


public interface GroupTrainingService {

    GroupTrainingsResponse getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException;

    GroupTrainingsPublicResponse getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException;

    GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException, InvalidHourException, InvalidDateException;

    //TEMPORARY commented
    /*List<GroupTrainingsResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
            throws NotExistingGroupTrainingException, InvalidDateException, InvalidHourException,
            StartDateAfterEndDateException, ParseException, TrainingTypeNotFoundException;

    List<GroupTrainingsPublicResponse> getGroupTrainingsPublicByType(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws TrainingTypeNotFoundException,
            NotExistingGroupTrainingException, InvalidDateException, InvalidHourException,
            StartDateAfterEndDateException, ParseException;*/

    GroupTrainingParticipantsResponse getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException;
}
