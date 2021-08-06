package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponseOld;

import java.text.ParseException;
import java.util.List;

public interface UniversalGroupTrainingService {

    List<GroupTrainingResponseOld> getGroupTrainingsWithParticipants(String startDate, String endDate)
            throws InvalidHourException,
            StartDateAfterEndDateException,
            ParseException,
            InvalidDateException;

    List<GroupTrainingPublicResponse> getGroupTrainingsWithoutParticipants(String startDate, String endDate)
            throws InvalidHourException,
            InvalidDateException,
            StartDateAfterEndDateException,
            ParseException;

    List<GroupTrainingResponseOld> getGroupTrainingsByTypeWithParticipants(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws NotExistingGroupTrainingException,
            InvalidDateException,
            InvalidHourException,
            StartDateAfterEndDateException,
            ParseException,
            TrainingTypeNotFoundException;

    List<GroupTrainingPublicResponse> getGroupTrainingsByTypeWithoutParticipants(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws TrainingTypeNotFoundException,
            NotExistingGroupTrainingException,
            InvalidDateException,
            InvalidHourException,
            StartDateAfterEndDateException,
            ParseException;
}
