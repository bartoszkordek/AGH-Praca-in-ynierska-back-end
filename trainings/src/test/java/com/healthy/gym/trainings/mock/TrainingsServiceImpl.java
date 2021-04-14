package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TrainingsServiceImpl extends TrainingsService {

    public TrainingsServiceImpl(TestRepository testRepository, GroupTrainingsDbRepository groupTrainingsDbRepository) {
        super(testRepository, groupTrainingsDbRepository);
    }

    @Autowired
    GroupTrainingsDbRepository groupTrainingsDbRepository;

    @Override
    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsDbRepository.getGroupTrainings();
    }

    @Override
    public GroupTrainings getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    @Override
    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if(trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    @Override
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
}