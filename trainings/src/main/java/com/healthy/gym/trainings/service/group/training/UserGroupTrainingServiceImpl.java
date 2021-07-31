package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsDbRepository groupTrainingsDbRepository;

    @Autowired
    public UserGroupTrainingServiceImpl(GroupTrainingsDbRepository groupTrainingsDbRepository) {
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepository.getMyAllGroupTrainings(clientId);
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if (!groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);

        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    private String getNotExistingGroupTrainingExceptionMessage(String trainingId) {
        return "Training with ID " + trainingId + " does not exist";
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        if (!groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
                && !groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)) {
            groupTrainingsDbRepository.removeFromParticipants(trainingId, clientId);
        }
        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId)) {
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
        }
    }
}
