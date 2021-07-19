package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingService {

    List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate) throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException;

    List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException;

    GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException, InvalidHourException, InvalidDateException;

    List<GroupTrainingResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate) throws NotExistingGroupTrainingException, InvalidDateException, InvalidHourException, StartDateAfterEndDateException, ParseException, TrainingTypeNotFoundException;

    List<GroupTrainingPublicResponse> getGroupTrainingsPublicByType(String trainingTypeId, String startDate, String endDate) throws TrainingTypeNotFoundException, NotExistingGroupTrainingException, InvalidDateException, InvalidHourException, StartDateAfterEndDateException, ParseException;

    List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId) throws InvalidHourException, InvalidDateException;

    List<String> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException;

    void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException;

    void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException;

    GroupTrainingResponse createGroupTraining(GroupTrainingRequest groupTrainingModel)
            throws TrainingCreationException, ParseException, InvalidHourException, InvalidDateException;

    GroupTrainingResponse removeGroupTraining(String trainingId)
            throws TrainingRemovalException, EmailSendingException, InvalidDateException, InvalidHourException;

    GroupTrainingResponse updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException, EmailSendingException, InvalidHourException, ParseException, InvalidDateException;

}
