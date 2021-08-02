package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final UserDAO userRepository;

    @Autowired
    public UserGroupTrainingServiceImpl(
            GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl,
            GroupTrainingsRepository groupTrainingsRepository,
            UserDAO userRepository
    ) {
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepositoryImpl.getMyAllGroupTrainings(clientId);
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException, NotExistingGroupTrainingException, UserNotFoundException {

        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTrainings == null) throw new NotExistingGroupTrainingException();

        UserDocument newParticipant = userRepository.findByUserId(clientId);
        if (newParticipant == null) throw new UserNotFoundException();

        if (!groupTrainingsDbRepositoryImpl.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        List<UserDocument> participants = groupTrainings.getParticipants();
        participants.add(newParticipant);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);

        // TODO nie rozumiem tego kodu poniżej, czemu on tutaj jest?
        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            removeFromReserveList(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTrainings == null) throw new NotExistingGroupTrainingException();

        UserDocument newReserveListParticipant = userRepository.findByUserId(clientId);
        if (newReserveListParticipant == null) throw new UserNotFoundException();

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        List<UserDocument> reserveList = groupTrainings.getReserveList();
        reserveList.add(newReserveListParticipant);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTrainings == null) throw new NotExistingGroupTrainingException();

        UserDocument participantToRemove = userRepository.findByUserId(clientId);
        if (participantToRemove == null) throw new UserNotFoundException();

        if (!groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
                && !groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (groupTrainingsDbRepositoryImpl.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)) {
            List<UserDocument> participants = groupTrainings.getParticipants();
            participants.remove(participantToRemove);
            groupTrainings.setParticipants(participants);
            groupTrainingsRepository.save(groupTrainings);
        }
        if (groupTrainingsDbRepositoryImpl.isClientAlreadyExistInReserveList(trainingId, clientId)) {
            removeFromReserveList(trainingId, clientId);
        }
    }

    private void removeFromReserveList(String trainingId, String clientId) {
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument reserveListParticipantToRemove = userRepository.findByUserId(clientId);
        List<UserDocument> reserveList = groupTrainings.getReserveList();
        reserveList.remove(reserveListParticipantToRemove);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }
}
