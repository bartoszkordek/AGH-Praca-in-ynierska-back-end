package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingService {

    List<GroupTrainingResponse> getGroupTrainings() throws InvalidHourException;

    List<GroupTrainingPublicResponse> getPublicGroupTrainings()
            throws InvalidHourException, InvalidDateException;

    GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws NotExistingGroupTrainingException, InvalidHourException;

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

}
