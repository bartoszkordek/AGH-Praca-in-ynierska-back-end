package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.*;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapToGroupTrainingsDocumentsToDTOs;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyEnrolledToGroupTraining;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.isClientAlreadyExistInReserveList;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final UserDAO userRepository;
    private final Pageable paging;
    private final ReviewDAO groupTrainingsReviewsRepository;
    private final Clock clock;

    @Autowired
    public UserGroupTrainingServiceImpl(
            GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl,
            GroupTrainingsRepository groupTrainingsRepository,
            GroupTrainingsDAO groupTrainingsDAO,
            UserDAO userRepository,
            ReviewDAO groupTrainingsReviewsRepository,
            Clock clock
    ) {
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.userRepository = userRepository;
        this.groupTrainingsReviewsRepository = groupTrainingsReviewsRepository;
        this.clock = clock;
        this.paging = PageRequest.of(0, 1000000);
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException, UserNotFoundException {

        UserDocument userDocument = userRepository.findByUserId(clientId);
        if (userDocument == null) throw new UserNotFoundException();

        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        List<GroupTrainingDocument> groupTrainings = groupTrainingsDAO.findByBasicListContains(userDocument);

        for (GroupTrainingDocument groupTrainingDocument : groupTrainings) {

            double rating = 0.0;
            if (!groupTrainings.isEmpty()) {
                List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository
                        .findByDateBetweenAndTrainingTypeId(
                                null,
                                null,
                                groupTrainingDocument.getTraining().getTrainingTypeId(),
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
                            groupTrainingDocument.getId(),
                            groupTrainingDocument.getTraining().getName(),
                            null, //TODO fix groupTraining.getTrainerId(),
                            groupTrainingDocument.getStartDate(),
                            groupTrainingDocument.getEndDate(),
                            groupTrainingDocument.getLocation().getName(),
                            groupTrainingDocument.getLimit(),
                            rating
                    )
            );
        }

        return publicResponse;
    }

    @Override
    public GroupTrainingDTO enrollToGroupTraining(String trainingId, String clientId)
            throws NotExistingGroupTrainingException,
            PastDateException,
            UserAlreadyEnrolledToTrainingException,
            UserNotFoundException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument user = userRepository.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();

        if (groupTraining.getStartDate().isAfter(LocalDateTime.now(clock))) throw new PastDateException();

        boolean clientIsInBasicList = isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId);
        boolean clientIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, clientId);

        if (clientIsInBasicList || clientIsInReserveList) throw new UserAlreadyEnrolledToTrainingException();

        if (groupTraining.getBasicList().size() < groupTraining.getLimit()) {
            enrollToBasicList(groupTraining, user);
        } else {
            enrollToReserveList(groupTraining, user);
        }

        GroupTrainingDocument groupTrainingUpdated = groupTrainingsDAO.save(groupTraining);

        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingUpdated);
    }

    private void enrollToBasicList(GroupTrainingDocument groupTraining, UserDocument user) {
        List<UserDocument> basicUsersList = groupTraining.getBasicList();
        basicUsersList.add(user);
    }

    private void enrollToReserveList(GroupTrainingDocument groupTraining, UserDocument user) {
        List<UserDocument> reserveUsersList = groupTraining.getReserveList();
        reserveUsersList.add(user);
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException, UserNotFoundException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        UserDocument participantToRemove = userRepository.findByUserId(clientId);
        if (participantToRemove == null) throw new UserNotFoundException();

        if (groupTraining.getStartDate().isAfter(LocalDateTime.now())) {
            throw new TrainingEnrollmentException("Retro event");
        }

        boolean clientIsEnrolled = isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId);
        boolean clientIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, clientId);

        if (!clientIsEnrolled && !clientIsInReserveList)
            throw new TrainingEnrollmentException("Client is not enrolled to this training");

        if (clientIsEnrolled) {
            List<UserDocument> participants = groupTraining.getBasicList();
            participants.remove(participantToRemove);
            groupTraining.setBasicList(participants);
            groupTrainingsDAO.save(groupTraining);
        }

        if (clientIsInReserveList) removeFromReserveList(trainingId, clientId);
    }

    private List<UserResponse> mapUserResponse(List<UserDocument> documents) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserDocument document : documents) {
            UserResponse userResponse = new UserResponse(
                    document.getUserId(),
                    document.getName(),
                    document.getSurname()
            );
            userResponses.add(userResponse);
        }
        return userResponses;
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
