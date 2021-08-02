package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyEnrolledToGroupTraining;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyExistInReserveList;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final UserDAO userRepository;
    private final Pageable paging;
    private final ReviewDAO groupTrainingsReviewsRepository;

    @Autowired
    public UserGroupTrainingServiceImpl(
            GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl,
            GroupTrainingsRepository groupTrainingsRepository,
            UserDAO userRepository,
            ReviewDAO groupTrainingsReviewsRepository
    ) {
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.userRepository = userRepository;
        this.groupTrainingsReviewsRepository = groupTrainingsReviewsRepository;
        this.paging = PageRequest.of(0, 1000000);
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException, UserNotFoundException {

        UserDocument userDocument = userRepository.findByUserId(clientId);
        if (userDocument == null) throw new UserNotFoundException();

        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        List<GroupTrainings> groupTrainings = groupTrainingsRepository
                .findGroupTrainingsByParticipantsContains(clientId);

        for (GroupTrainings groupTraining : groupTrainings) {

            double rating = 0.0;
            if (!groupTrainings.isEmpty()) {
                List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository
                        .findByDateBetweenAndTrainingTypeId(
                                null,
                                null,
                                groupTraining.getTrainingType().getTrainingTypeId(),
                                paging
                        ).getContent();

                double sum = 0;
                int counter = 0;
                for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
                    sum += review.getStars();
                    counter++;
                }
                if (counter != 0) rating = sum / counter;
            }

            publicResponse.add(
                    new GroupTrainingPublicResponse(
                            groupTraining.getTrainingId(),
                            groupTraining.getTrainingType().getName(),
                            null, //TODO fix groupTraining.getTrainerId(),
                            null,//TODO fix groupTraining.getStartDate(),
                            null,//TODO fix groupTraining.getEndDate(),
                            null,//TODO fix groupTraining.getLocation().getName()
                            groupTraining.getLimit(),
                            rating
                    )
            );
        }

        return publicResponse;
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId)
            throws TrainingEnrollmentException, NotExistingGroupTrainingException, UserNotFoundException {

        GroupTrainings groupTraining = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument newParticipant = userRepository.findByUserId(clientId);
        if (newParticipant == null) throw new UserNotFoundException();

        if (!groupTrainingsDbRepositoryImpl.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");

        if (isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        List<UserDocument> participants = groupTraining.getParticipants();
        participants.add(newParticipant);
        groupTraining.setParticipants(participants);
        groupTrainingsRepository.save(groupTraining);

        // TODO nie rozumiem tego kodu poniżej, czemu on tutaj jest?
        if (isClientAlreadyExistInReserveList(groupTraining, clientId))
            removeFromReserveList(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainings groupTraining = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument newReserveListParticipant = userRepository.findByUserId(clientId);
        if (newReserveListParticipant == null) throw new UserNotFoundException();

        if (isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        if (isClientAlreadyExistInReserveList(groupTraining, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        List<UserDocument> reserveList = groupTraining.getReserveList();
        reserveList.add(newReserveListParticipant);
        groupTraining.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTraining);
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainings groupTraining = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument participantToRemove = userRepository.findByUserId(clientId);
        if (participantToRemove == null) throw new UserNotFoundException();

        boolean clientIsEnrolled = isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId);
        boolean clientIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, clientId);

        if (!clientIsEnrolled && !clientIsInReserveList)
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (clientIsEnrolled) {
            List<UserDocument> participants = groupTraining.getParticipants();
            participants.remove(participantToRemove);
            groupTraining.setParticipants(participants);
            groupTrainingsRepository.save(groupTraining);
        }

        if (clientIsInReserveList) removeFromReserveList(trainingId, clientId);
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
