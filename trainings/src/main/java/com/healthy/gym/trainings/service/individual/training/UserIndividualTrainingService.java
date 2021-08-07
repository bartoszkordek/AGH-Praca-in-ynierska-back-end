package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;

import java.util.List;

public interface UserIndividualTrainingService {

    List<IndividualTrainingDTO> getMyAllTrainings(String clientId)
            throws NoIndividualTrainingFoundException, UserNotFoundException;

    IndividualTrainingDTO createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel, String clientId
    ) throws PastDateException, StartDateAfterEndDateException, TrainerOccupiedException,
            TrainerNotFoundException, UserNotFoundException;

    IndividualTrainingDTO cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException, UserNotFoundException, PastDateException;
}
