package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.HallNoOutOfRangeException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.model.IndividualTrainingsAcceptModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
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

    @Override
    public IndividualTrainings createIndividualTrainingRequest(IndividualTrainingsRequestModel individualTrainingsRequestModel,
                                                               String clientId) throws InvalidHourException {
        return individualTrainingsDbRepository.createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);

    }

    @Override
    public IndividualTrainings acceptIndividualTraining(String trainingId, IndividualTrainingsAcceptModel individualTrainingsAcceptModel) throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException {
        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
        }
        if(individualTrainingsDbRepository.isIndividualTrainingExistAndAccepted(trainingId)){
            throw new AlreadyAcceptedIndividualTrainingException("Training with ID: "+ trainingId + " has been already accepted");
        }
        if(individualTrainingsAcceptModel.getHallNo() < 0){
            throw new HallNoOutOfRangeException("Hall no: " + individualTrainingsAcceptModel.getHallNo() + " does not exist");
        }
        return individualTrainingsDbRepository.acceptIndividualTrainingRequest(trainingId, individualTrainingsAcceptModel);
    }
}
