package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
public class TrainingsService {
    TestRepository testRepository;
    GroupTrainingsDbRepository groupTrainingsDbRepository;

    public TrainingsService(TestRepository testRepository,
                            GroupTrainingsDbRepository groupTrainingsDbRepository){
        this.testRepository = testRepository;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
    }

    public String getFirstTestDocument(){
        return testRepository.getFirstTestDocument();
    }

    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsDbRepository.getGroupTrainings();
    }

    public GroupTrainings getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
    }

    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if(trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);

        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
    }

    public void addToReserveList(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    public void removeGroupTrainingEnrollment(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if(!groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
           && !groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)){
            groupTrainingsDbRepository.removeFromParticipants(trainingId, clientId);
        }
        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId)){
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
        }
    }

    public GroupTrainings createGroupTraining(GroupTrainingModel groupTrainingModel) throws TrainingCreationException, ParseException {
        if(!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModel))
            throw new TrainingCreationException("Cannot create new group training");

        return groupTrainingsDbRepository.createTraining(groupTrainingModel);
    }

    public GroupTrainings removeGroupTraining(String trainingId) throws TrainingRemovalException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: "+ trainingId + " doesn't exist");
        return groupTrainingsDbRepository.removeTraining(trainingId);
    }

    public GroupTrainings updateGroupTraining(String trainingId, GroupTrainingModel groupTrainingModelRequest) throws TrainingUpdateException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingUpdateException("Training with ID: "+ trainingId + " doesn't exist");
        return groupTrainingsDbRepository.updateTraining(trainingId, groupTrainingModelRequest);
    }
}
