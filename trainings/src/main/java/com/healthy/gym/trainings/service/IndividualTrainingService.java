package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;

import java.text.ParseException;
import java.util.List;

public interface IndividualTrainingService {
    List<IndividualTrainings> getAllIndividualTrainings();

    IndividualTrainings getIndividualTrainingById(String trainingId)
            throws NotExistingIndividualTrainingException;

    List<IndividualTrainings> getMyAllTrainings(String clientId);

    List<IndividualTrainings> getAllAcceptedIndividualTrainings();

    IndividualTrainings createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel,
            String clientId
    ) throws InvalidHourException, ParseException, RetroIndividualTrainingException;

    IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException,
            NotAuthorizedClientException,
            ParseException,
            RetroIndividualTrainingException;

    IndividualTrainings acceptIndividualTraining(
            String trainingId,
            IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel
    ) throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException,
            HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException,
            EmailSendingException;

    IndividualTrainings rejectIndividualTraining(String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyDeclinedIndividualTrainingException,
            EmailSendingException;
}
