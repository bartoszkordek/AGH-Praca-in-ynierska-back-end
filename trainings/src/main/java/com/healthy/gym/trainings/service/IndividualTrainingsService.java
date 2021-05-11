package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.IndividualTrainingsAcceptModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class IndividualTrainingsService {

    IndividualTrainingsDbRepository individualTrainingsDbRepository;

    public IndividualTrainingsService(IndividualTrainingsDbRepository individualTrainingsDbRepository){
        this.individualTrainingsDbRepository = individualTrainingsDbRepository;
    }

    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
        String startDateAndTime = date.concat("-").concat(startDate);
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);

        Date now = new Date();

        if(requestDateParsed.before(now)) return true;

        return false;
    }

    public List<IndividualTrainings> getAllIndividualTrainings(){
        return individualTrainingsDbRepository.getIndividualTrainings();
    }

    public IndividualTrainings getIndividualTrainingById(String trainingId) throws NotExistingIndividualTrainingException {
        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
        }
        return individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
    }

    public List<IndividualTrainings> getAllAcceptedIndividualTrainings(){
        return individualTrainingsDbRepository.getAcceptedIndividualTrainings();
    }

    public IndividualTrainings createIndividualTrainingRequest(IndividualTrainingsRequestModel individualTrainingsRequestModel,
                                                               String clientId) throws InvalidHourException {
        return individualTrainingsDbRepository.createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);

    }

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

    public IndividualTrainings declineIndividualTraining(String trainingId) throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException {
        if(!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)){
            throw new NotExistingIndividualTrainingException("Training with ID: "+ trainingId + " doesn't exist");
        }
        if(individualTrainingsDbRepository.isIndividualTrainingExistAndDeclined(trainingId)){
            throw new AlreadyDeclinedIndividualTrainingException("Training with ID: "+ trainingId + " has been already declined");
        }
        return individualTrainingsDbRepository.declineIndividualTrainingRequest(trainingId);
    }

    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId) throws NotExistingIndividualTrainingException, NotAuthorizedClientException, ParseException, RetroIndividualTrainingException {
        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId + " doesn't exist");
        }
        if (!individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(trainingId, clientId)) {
            throw new NotAuthorizedClientException("Training is not authorized by client");
        }
        IndividualTrainings individualTraining = individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();
        if(isTrainingRetroDateAndTime(individualTrainingDate ,individualTrainingStartTime)){
            throw new RetroIndividualTrainingException("Retro date");
        }
        return individualTrainingsDbRepository.cancelIndividualTrainingRequest(trainingId);
    }
}
