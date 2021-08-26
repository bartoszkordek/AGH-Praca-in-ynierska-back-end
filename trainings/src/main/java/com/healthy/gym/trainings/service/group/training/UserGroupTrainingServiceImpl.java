package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.group.training.UserGroupTrainingsDAO;
import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.utils.GroupTrainingMapper;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingsDocumentToDTO;
import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapGroupTrainingToBasicTrainingDTO;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.*;

@Service
public class UserGroupTrainingServiceImpl implements UserGroupTrainingService {

    private final UserGroupTrainingsDAO userGroupTrainingsDAO;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final UserDAO userDAO;
    private final Clock clock;

    @Autowired
    public UserGroupTrainingServiceImpl(
            UserGroupTrainingsDAO userGroupTrainingsDAO,
            GroupTrainingsDAO groupTrainingsDAO,
            UserDAO userDAO,
            Clock clock
    ) {
        this.userGroupTrainingsDAO = userGroupTrainingsDAO;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.userDAO = userDAO;
        this.clock = clock;
    }

    @Override
    public List<GroupTrainingDTO> getMyAllTrainings(String clientId, String startDate, String endDate)
            throws UserNotFoundException, StartDateAfterEndDateException {

        UserDocument user = userDAO.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTIme = validator.getEndOfEndDate();

        List<GroupTrainingDocument> groupTrainingDocumentList = userGroupTrainingsDAO
                .findAllGroupTrainings(user, startDateTime, endDateTIme);

        if (groupTrainingDocumentList.isEmpty()) return List.of();

        return groupTrainingDocumentList
                .stream()
                .map(GroupTrainingMapper::mapGroupTrainingsDocumentToDTO)
                .collect(Collectors.toList());
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
        return mapGroupTrainingsDocumentToDTO(groupTrainingUpdated);
    }

    private GroupTrainingDocument getAndCheckGroupTraining(String trainingId)
            throws NotExistingGroupTrainingException {
        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();
        return groupTraining;
    }

    private UserDocument getAndCheckUser(String userId) throws UserNotFoundException {
        UserDocument user = userDAO.findByUserId(userId);
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
        return mapGroupTrainingsDocumentToDTO(groupTrainingUpdated);
    }

    @Override
    public BasicTrainingDTO getMyNextTraining(String clientId) throws UserNotFoundException {

        UserDocument user = userDAO.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();

        List<GroupTrainingDocument> groupTrainingDocuments = userGroupTrainingsDAO
                .findAllGroupTrainingsByUserAndStartDateAfterNow(user);

        Optional<GroupTrainingDocument> nextUserGroupTrainingDocumentOptional = groupTrainingDocuments
                .stream()
                .sorted(Comparator.nullsLast((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate())))
                .findFirst();

        if(nextUserGroupTrainingDocumentOptional.isEmpty()) return null;

        GroupTrainingDocument nextUserGroupTrainingDocument = nextUserGroupTrainingDocumentOptional.get();

        return mapGroupTrainingToBasicTrainingDTO(nextUserGroupTrainingDocument);
    }
}
