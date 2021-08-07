package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;

import java.util.List;

public interface EmployeeIndividualTrainingService {
    List<IndividualTrainingDTO> getIndividualTrainings(String startDate, String endDate, int page, int size)
            throws StartDateAfterEndDateException, NoIndividualTrainingFoundException;

    IndividualTrainingDTO getIndividualTrainingById(String trainingId) throws NotExistingIndividualTrainingException;

    List<IndividualTrainingDTO> getAllAcceptedIndividualTrainings(String startDate, String endDate, int page, int size)
            throws StartDateAfterEndDateException, NoIndividualTrainingFoundException;
}
