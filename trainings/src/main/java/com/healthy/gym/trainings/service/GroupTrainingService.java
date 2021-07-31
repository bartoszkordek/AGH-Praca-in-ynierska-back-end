package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;

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

    List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException;

    List<ParticipantsResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException;

    void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException;

    void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;
}
