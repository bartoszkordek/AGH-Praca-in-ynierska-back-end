package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidTrainerSpecifiedException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;

import java.util.List;

public interface UserIndividualTrainingService {

    List<IndividualTrainingDTO> getMyAllTrainings(String clientId, String startDateTime, String endDateTime)
            throws NoIndividualTrainingFoundException, UserNotFoundException, StartDateAfterEndDateException;

    IndividualTrainingDTO createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel, String clientId
    ) throws PastDateException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException,
            UserNotFoundException,
            InvalidTrainerSpecifiedException;

    IndividualTrainingDTO cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException,
            UserNotFoundException,
            PastDateException,
            UserIsNotParticipantException,
            IndividualTrainingHasBeenRejectedException,
            AlreadyCancelledIndividualTrainingException;

    BasicTrainingDTO getMyNextTraining(String clientId) throws UserNotFoundException;
}
