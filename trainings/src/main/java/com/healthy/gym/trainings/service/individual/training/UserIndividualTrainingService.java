package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.RetroIndividualTrainingException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;

import java.text.ParseException;
import java.util.List;

public interface UserIndividualTrainingService {

    List<IndividualTrainings> getMyAllTrainings(String clientId) throws UserNotFoundException;

    IndividualTrainings createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel,
            String clientId
    ) throws InvalidHourException, ParseException, RetroIndividualTrainingException;

    IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException,
            NotAuthorizedClientException,
            ParseException,
            RetroIndividualTrainingException;
}
