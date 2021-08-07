package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;

import java.text.ParseException;

public interface TrainerIndividualTrainingService {

    IndividualTrainingDTO acceptIndividualTraining(
            String trainingId,
            IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel
    ) throws AlreadyAcceptedIndividualTrainingException, NotExistingIndividualTrainingException,
            LocationNotFoundException, LocationOccupiedException, PastDateException,
            HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException,
            EmailSendingException;

    IndividualTrainingDTO rejectIndividualTraining(String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyDeclinedIndividualTrainingException,
            EmailSendingException;
}
