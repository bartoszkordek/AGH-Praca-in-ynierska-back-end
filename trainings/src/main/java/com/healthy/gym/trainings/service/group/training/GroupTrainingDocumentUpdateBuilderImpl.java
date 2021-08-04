package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupTrainingDocumentUpdateBuilderImpl implements GroupTrainingDocumentUpdateBuilder {

    private final TrainingTypeDAO trainingTypeDAO;
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;
    private final Clock clock;

    private GroupTrainingDocument currentGroupTrainingDocument;
    private ManagerGroupTrainingRequest groupTrainingRequest;

    @Autowired
    public GroupTrainingDocumentUpdateBuilderImpl(
            TrainingTypeDAO trainingTypeDAO,
            LocationDAO locationDAO,
            UserDAO userDAO,
            Clock clock
    ) {
        this.trainingTypeDAO = trainingTypeDAO;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
        this.clock = clock;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder setGroupTrainingDocumentToUpdate(GroupTrainingDocument groupTraining) {
        this.currentGroupTrainingDocument = groupTraining;
        return this;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder setGroupTrainingRequest(ManagerGroupTrainingRequest groupTrainingRequest) {
        this.groupTrainingRequest = groupTrainingRequest;
        return this;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateTrainingType() throws TrainingTypeNotFoundException {
        validateState();

        TrainingTypeDocument trainingTypeDocument = currentGroupTrainingDocument.getTraining();
        String currentTrainingTypeId = trainingTypeDocument.getTrainingTypeId();
        String trainingTypeIdToUpdate = groupTrainingRequest.getTrainingTypeId();

        boolean trainingTypeIdIsTheSame = currentTrainingTypeId.equals(trainingTypeIdToUpdate);

        if (!trainingTypeIdIsTheSame) {
            TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeIdToUpdate);
            if (trainingType == null) throw new TrainingTypeNotFoundException();
            currentGroupTrainingDocument.setTraining(trainingType);
        }

        return this;
    }

    private void validateState() {
        if (groupTrainingRequest == null || currentGroupTrainingDocument == null)
            throw new IllegalStateException("GroupTrainingDocument and ManagerGroupTrainingRequest not provided!");
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateTrainers() throws TrainerNotFoundException {
        validateState();

        List<String> currentTrainerIds = currentGroupTrainingDocument
                .getTrainers()
                .stream()
                .map(UserDocument::getUserId)
                .collect(Collectors.toList());
        List<String> trainerIds = groupTrainingRequest.getTrainerIds();

        boolean trainerIdsAreTheSame = currentTrainerIds.equals(trainerIds);

        if (!trainerIdsAreTheSame) {
            List<UserDocument> trainers = getListOfTrainersUserDocument(trainerIds);
            currentGroupTrainingDocument.setTrainers(trainers);
        }

        return this;
    }

    private List<UserDocument> getListOfTrainersUserDocument(List<String> trainerIds)
            throws TrainerNotFoundException {
        List<UserDocument> trainers = new ArrayList<>();
        for (String trainerId : trainerIds) {
            UserDocument trainer = userDAO.findByUserId(trainerId);
            if (trainer == null || !trainer.getGymRoles().contains(GymRole.TRAINER))
                throw new TrainerNotFoundException();
            trainers.add(trainer);
        }
        return trainers;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateStartDate() throws PastDateException {
        validateState();

        LocalDateTime currentStartDateTime = currentGroupTrainingDocument.getStartDate();
        String startDateTimeToUpdateStr = groupTrainingRequest.getStartDate();
        LocalDateTime startDateTimeToUpdate = parseDateTime(startDateTimeToUpdateStr);

        boolean startTimeIsTheSame = currentStartDateTime.equals(startDateTimeToUpdate);

        if (!startTimeIsTheSame) {
            if (startDateTimeToUpdate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
            currentGroupTrainingDocument.setStartDate(startDateTimeToUpdate);
        }

        return this;
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateEndDate() throws PastDateException {
        validateState();

        LocalDateTime currentEndDateTime = currentGroupTrainingDocument.getEndDate();
        String endDateTimeToUpdateStr = groupTrainingRequest.getEndDate();
        LocalDateTime endDateTimeToUpdate = LocalDateTime
                .parse(endDateTimeToUpdateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        boolean endTimeIsTheSame = currentEndDateTime.equals(endDateTimeToUpdate);

        if (!endTimeIsTheSame) {
            if (endDateTimeToUpdate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
            currentGroupTrainingDocument.setEndDate(endDateTimeToUpdate);
        }

        return this;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateLocation() throws LocationNotFoundException {
        validateState();

        LocationDocument currentLocation = currentGroupTrainingDocument.getLocation();
        String currentLocationId = currentLocation.getLocationId();
        String locationIdToUpdate = groupTrainingRequest.getLocationId();
        boolean locationIdIsTheSame = currentLocationId.equals(locationIdToUpdate);

        if (!locationIdIsTheSame) {
            LocationDocument locationDocument = locationDAO.findByLocationId(locationIdToUpdate);
            if (locationDocument == null) throw new LocationNotFoundException();
            currentGroupTrainingDocument.setLocation(locationDocument);
        }

        return this;
    }

    @Override
    public GroupTrainingDocumentUpdateBuilder updateLimit() {
        validateState();

        int currentLimit = currentGroupTrainingDocument.getLimit();
        int limitToUpdate = groupTrainingRequest.getLimit();
        boolean limitIsTheSame = currentLimit == limitToUpdate;

        if (!limitIsTheSame) currentGroupTrainingDocument.setLimit(limitToUpdate);

        return this;
    }

    @Override
    public GroupTrainingDocument update() {
        validateState();
        return this.currentGroupTrainingDocument;
    }
}
