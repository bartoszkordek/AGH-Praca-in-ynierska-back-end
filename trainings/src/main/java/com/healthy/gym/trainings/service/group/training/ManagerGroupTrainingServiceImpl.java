package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapToGroupTrainingsDocumentsToDTOs;

@Service
public class ManagerGroupTrainingServiceImpl implements ManagerGroupTrainingService {

    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;
    private final Clock clock;
    private final GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder;

    @Autowired
    public ManagerGroupTrainingServiceImpl(
            GroupTrainingsDAO groupTrainingsDAO,
            TrainingTypeDAO trainingTypeDAO,
            LocationDAO locationDAO,
            UserDAO userDAO,
            Clock clock,
            GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder
    ) {
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
        this.clock = clock;
        this.groupTrainingDocumentUpdateBuilder = groupTrainingDocumentUpdateBuilder;
    }

    @Override
    public GroupTrainingDTO createGroupTraining(
            final ManagerGroupTrainingRequest createGroupTrainingRequest
    ) throws StartDateAfterEndDateException,
            TrainerNotFoundException,
            LocationNotFoundException,
            TrainingTypeNotFoundException,
            LocationOccupiedException,
            TrainerOccupiedException,
            PastDateException {

        TrainingTypeDocument trainingType = getTrainingTypeDocument(createGroupTrainingRequest);
        List<UserDocument> trainers = getListOfTrainersUserDocument(createGroupTrainingRequest);
        LocationDocument location = getLocationDocument(createGroupTrainingRequest);
        LocalDateTime startDate = getStartDate(createGroupTrainingRequest);
        LocalDateTime endDate = getEndDate(createGroupTrainingRequest);
        int limit = createGroupTrainingRequest.getLimit();

        GroupTrainingDocument groupTrainingToCreate = new GroupTrainingDocument(
                UUID.randomUUID().toString(),
                trainingType,
                trainers,
                startDate,
                endDate,
                location,
                limit,
                new ArrayList<>(),
                new ArrayList<>()
        );

        validateStartDateTime(groupTrainingToCreate);
        checkIfStartDateTimeIsBeforeEndDateTime(groupTrainingToCreate);
        //TODO add validation LocationOccupiedException
        //TODO add validation TrainerOccupiedException

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingToCreate);
        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingSaved);
    }

    private TrainingTypeDocument getTrainingTypeDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws TrainingTypeNotFoundException {
        String trainingTypeId = groupTrainingRequest.getTrainingTypeId();
        TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
        if (trainingType == null) throw new TrainingTypeNotFoundException();
        return trainingType;
    }

    private List<UserDocument> getListOfTrainersUserDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws TrainerNotFoundException {

        List<String> trainerIds = groupTrainingRequest.getTrainerIds();
        List<UserDocument> trainers = new ArrayList<>();
        for (String trainerId : trainerIds) {
            UserDocument trainer = userDAO.findByUserId(trainerId);
            if (trainer == null || !trainer.getGymRoles().contains(GymRole.TRAINER))
                throw new TrainerNotFoundException();
            trainers.add(trainer);
        }
        return trainers;
    }

    private LocationDocument getLocationDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws LocationNotFoundException {
        String locationId = groupTrainingRequest.getLocationId();
        LocationDocument location = locationDAO.findByLocationId(locationId);
        if (location == null) throw new LocationNotFoundException();
        return location;
    }

    private LocalDateTime getStartDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String startDate = groupTrainingRequest.getStartDate();
        return parseDateTime(startDate);
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private LocalDateTime getEndDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String endDate = groupTrainingRequest.getEndDate();
        return parseDateTime(endDate);
    }

    private void validateStartDateTime(GroupTrainingDocument groupTraining) throws PastDateException {
        LocalDateTime startDate = groupTraining.getStartDate();
        if (startDate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
    }

    private void checkIfStartDateTimeIsBeforeEndDateTime(GroupTrainingDocument groupTraining)
            throws StartDateAfterEndDateException {
        LocalDateTime startDate = groupTraining.getStartDate();
        LocalDateTime endDate = groupTraining.getEndDate();
        if (endDate.isBefore(startDate)) throw new StartDateAfterEndDateException();
    }

    @Override
    public GroupTrainingDTO updateGroupTraining(
            final String trainingId,
            final ManagerGroupTrainingRequest groupTrainingRequest
    ) throws LocationNotFoundException,
            LocationOccupiedException,
            NotExistingGroupTrainingException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerNotFoundException,
            TrainerOccupiedException,
            TrainingTypeNotFoundException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        GroupTrainingDocument groupTrainingUpdated = groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(groupTraining)
                .setGroupTrainingRequest(groupTrainingRequest)
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update();

        checkIfStartDateTimeIsBeforeEndDateTime(groupTrainingUpdated);
        //TODO add validation LocationOccupiedException
        //TODO add validation TrainerOccupiedException

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingUpdated);

        sendEmails(groupTrainingSaved);

        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingSaved);
    }

    private void sendEmails(GroupTrainingDocument groupTraining) {
        LocalDateTime endDate = groupTraining.getEndDate();
        if (endDate.isAfter(LocalDateTime.now(clock))) return;

        List<String> emails = getAllEmails(groupTraining);
        //TODO send emails to all participants about changes
    }

    private List<String> getAllEmails(GroupTrainingDocument groupTraining) {
        Set<UserDocument> allGroupTrainingUsers = new HashSet<>();
        allGroupTrainingUsers.addAll(groupTraining.getTrainers());
        allGroupTrainingUsers.addAll(groupTraining.getBasicList());
        allGroupTrainingUsers.addAll(groupTraining.getReserveList());

        return allGroupTrainingUsers
                .stream()
                .map(UserDocument::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public GroupTrainingDTO removeGroupTraining(String trainingId) throws NotExistingGroupTrainingException {

        GroupTrainingDocument groupTrainingToDelete = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTrainingToDelete == null) throw new NotExistingGroupTrainingException();
        groupTrainingsDAO.delete(groupTrainingToDelete);

        sendEmails(groupTrainingToDelete);

        return mapToGroupTrainingsDocumentsToDTOs(groupTrainingToDelete);
    }
}
