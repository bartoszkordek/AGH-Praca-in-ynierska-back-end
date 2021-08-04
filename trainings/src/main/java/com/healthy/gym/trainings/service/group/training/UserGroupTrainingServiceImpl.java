package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
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
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.*;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final GroupTrainingsDAO groupTrainingsDAO;
    private final UserDAO userRepository;
    private final ReviewDAO groupTrainingsReviewsRepository;
    private final Clock clock;
    private final Pageable paging;

    @Autowired
    public UserGroupTrainingServiceImpl(
            GroupTrainingsDAO groupTrainingsDAO,
            UserDAO userRepository,
            ReviewDAO groupTrainingsReviewsRepository,
            Clock clock
    ) {
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
    public GroupTrainingDTO enrollToGroupTraining(String trainingId, String userId)
            throws NotExistingGroupTrainingException, PastDateException,
            UserAlreadyEnrolledToTrainingException, UserNotFoundException {

        GroupTrainingDocument groupTraining = getAndCheckGroupTraining(trainingId);
        UserDocument user = getAndCheckUser(userId);
        validateTime(groupTraining);

        boolean userIsInBasicList = isClientAlreadyEnrolledToGroupTraining(groupTraining, userId);
        boolean userIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, userId);
        if (userIsInBasicList || userIsInReserveList) throw new UserAlreadyEnrolledToTrainingException();

        if (groupTraining.getBasicList().size() < groupTraining.getLimit()) {
            enrollToBasicList(groupTraining, user);
        } else {
            enrollToReserveList(groupTraining, user);
        }

        GroupTrainingDocument groupTrainingUpdated = groupTrainingsDAO.save(groupTraining);
        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingUpdated);
    }

    private GroupTrainingDocument getAndCheckGroupTraining(String trainingId)
            throws NotExistingGroupTrainingException {
        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();
        return groupTraining;
    }

    private UserDocument getAndCheckUser(String userId) throws UserNotFoundException {
        UserDocument user = userRepository.findByUserId(userId);
        if (user == null) throw new UserNotFoundException();
        return user;
    }

    private void validateTime(GroupTrainingDocument groupTraining) throws PastDateException {
        if (LocalDateTime.now(clock).isAfter(groupTraining.getStartDate())) throw new PastDateException();
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
    public GroupTrainingDTO removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, PastDateException,
            UserNotFoundException, TrainingEnrollmentException {

        GroupTrainingDocument groupTraining = getAndCheckGroupTraining(trainingId);
        getAndCheckUser(clientId);
        validateTime(groupTraining);

        boolean userIsInBasicList = isClientAlreadyEnrolledToGroupTraining(groupTraining, clientId);
        boolean userIsInReserveList = isClientAlreadyExistInReserveList(groupTraining, clientId);
        if (!userIsInBasicList && !userIsInReserveList) throw new TrainingEnrollmentException();

        if (userIsInBasicList) removeFromBasicList(groupTraining, clientId);
        if (userIsInReserveList) removeFromReserveList(groupTraining, clientId);

        GroupTrainingDocument groupTrainingUpdated = groupTrainingsDAO.save(groupTraining);
        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingUpdated);
    }
}
