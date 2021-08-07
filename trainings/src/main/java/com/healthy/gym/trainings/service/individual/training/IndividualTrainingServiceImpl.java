package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsRepository;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndividualTrainingServiceImpl implements IndividualTrainingService {

    private final IndividualTrainingsRepository individualTrainingsRepository;

    @Autowired
    public IndividualTrainingServiceImpl(IndividualTrainingsRepository individualTrainingsRepository) {
        this.individualTrainingsRepository = individualTrainingsRepository;
    }

    @Override
    public List<IndividualTrainings> getAllIndividualTrainings() {
        return individualTrainingsRepository.findAll();
    }

    @Override
    public IndividualTrainings getIndividualTrainingById(String trainingId)
            throws NotExistingIndividualTrainingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);
        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        return individualTraining;
    }

    @Override
    public List<IndividualTrainings> getAllAcceptedIndividualTrainings() {
        return individualTrainingsRepository.findAllByAccepted(true);
    }
}
