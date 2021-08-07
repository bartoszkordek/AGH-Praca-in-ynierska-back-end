package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;

import java.text.ParseException;

public interface TrainerIndividualTrainingService {

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
