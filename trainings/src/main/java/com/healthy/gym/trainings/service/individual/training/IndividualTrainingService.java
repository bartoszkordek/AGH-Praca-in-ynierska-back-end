package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;

import java.util.List;

public interface IndividualTrainingService {
    List<IndividualTrainings> getAllIndividualTrainings();

    IndividualTrainings getIndividualTrainingById(String trainingId)
            throws NotExistingIndividualTrainingException;

    List<IndividualTrainings> getAllAcceptedIndividualTrainings();

}
