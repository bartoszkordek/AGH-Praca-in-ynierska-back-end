package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
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

    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;

    @Autowired
    public UserGroupTrainingServiceImpl(GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl) {
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepositoryImpl.getMyAllGroupTrainings(clientId);
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if (!groupTrainingsDbRepositoryImpl.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepositoryImpl.enrollToGroupTraining(trainingId, clientId);

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepositoryImpl.removeFromReserveList(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepositoryImpl.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepositoryImpl.addToReserveList(trainingId, clientId);
    }

    private String getNotExistingGroupTrainingExceptionMessage(String trainingId) {
        return "Training with ID " + trainingId + " does not exist";
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepositoryImpl.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        if (!groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
                && !groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)) {
            groupTrainingsDbRepositoryImpl.removeFromParticipants(trainingId, clientId);
        }
        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId)) {
            groupTrainingsDbRepositoryImpl.removeFromReserveList(trainingId, clientId);
        }
    }
}
