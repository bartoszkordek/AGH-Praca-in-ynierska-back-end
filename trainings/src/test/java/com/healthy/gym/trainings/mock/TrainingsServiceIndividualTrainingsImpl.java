package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import org.springframework.beans.factory.annotation.Autowired;

public class TrainingsServiceIndividualTrainingsImpl extends IndividualTrainingsService {

    @Autowired
    IndividualTrainingsDbRepository individualTrainingsDbRepository;

    public TrainingsServiceIndividualTrainingsImpl(IndividualTrainingsDbRepository individualTrainingsDbRepository) {
        super(individualTrainingsDbRepository);
    }

    @Override
    public IndividualTrainings getIndividualTrainingById(String trainingId) throws NotExistingIndividualTrainingException {
        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
        }
        return individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
    }
}
