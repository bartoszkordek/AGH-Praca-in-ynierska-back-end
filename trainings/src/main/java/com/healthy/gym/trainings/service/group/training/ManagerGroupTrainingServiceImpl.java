package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.component.EmailSender;
import com.healthy.gym.trainings.data.document.*;
import com.healthy.gym.trainings.data.repository.*;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapToGroupTrainingDTO;

@Service
public class ManagerGroupTrainingServiceImpl implements ManagerGroupTrainingService {

    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;
    private final EmailSender emailSender;
    private final Clock clock;
    private final GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    public ManagerGroupTrainingServiceImpl(
            GroupTrainingsDAO groupTrainingsDAO,
            TrainingTypeDAO trainingTypeDAO,
            LocationDAO locationDAO,
            UserDAO userDAO,
            EmailSender emailSender,
            Clock clock,
            GroupTrainingsRepository groupTrainingsRepository
    ) {
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
        this.emailSender = emailSender;
        this.clock = clock;
        this.groupTrainingsRepository = groupTrainingsRepository;
    }

    @Override
    public GroupTrainingDTO createGroupTraining(final ManagerGroupTrainingRequest createGroupTrainingRequest)
            throws StartDateAfterEndDateException,
            TrainerNotFoundException,
            LocationNotFoundException,
            TrainingTypeNotFoundException,
            LocationOccupiedException,
            TrainerOccupiedException,
            PastDateException {

        TrainingTypeDocument trainingType = getTrainingTypeDocument(createGroupTrainingRequest);

        List<String> trainerIds = createGroupTrainingRequest.getTrainerIds();
        List<UserDocument> trainers = getListOfTrainersUserDocument(trainerIds);

        LocationDocument location = getLocationDocument(createGroupTrainingRequest);

        LocalDateTime startDate = parseStartDate(createGroupTrainingRequest);
        if (startDate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();

        LocalDateTime endDate = parseEndDate(createGroupTrainingRequest);
        if (endDate.isBefore(startDate)) throw new StartDateAfterEndDateException();

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

        //TODO add validation LocationOccupiedException

        //TODO add validation TrainerOccupiedException

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingToCreate);
        return mapToGroupTrainingDTO(groupTrainingSaved);
    }

    private TrainingTypeDocument getTrainingTypeDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws TrainingTypeNotFoundException {
        String trainingTypeId = groupTrainingRequest.getTrainingTypeId();
        TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
        if (trainingType == null) throw new TrainingTypeNotFoundException();
        return trainingType;
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

    private LocationDocument getLocationDocument(ManagerGroupTrainingRequest groupTrainingRequest)
            throws LocationNotFoundException {
        String locationId = groupTrainingRequest.getLocationId();
        LocationDocument location = locationDAO.findByLocationId(locationId);
        if (location == null) throw new LocationNotFoundException();
        return location;
    }

    private LocalDateTime parseStartDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String startDate = groupTrainingRequest.getStartDate();
        return parseDateTime(startDate);
    }

    private LocalDateTime parseDateTime(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private LocalDateTime parseEndDate(ManagerGroupTrainingRequest groupTrainingRequest) {
        String endDate = groupTrainingRequest.getEndDate();
        return parseDateTime(endDate);
    }

    @Override
    public GroupTrainingDTO updateGroupTraining(
            final String trainingId,
            final ManagerGroupTrainingRequest groupTrainingRequest
    ) throws TrainingTypeNotFoundException,
            NotExistingGroupTrainingException,
            TrainerNotFoundException,
            LocationNotFoundException,
            LocationOccupiedException,
            TrainerOccupiedException,
            PastDateException, StartDateAfterEndDateException {

        GroupTrainingDocument groupTraining = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);
        if (groupTraining == null) throw new NotExistingGroupTrainingException();

        updateTrainingTypeIfNeeded(groupTraining, groupTrainingRequest);
        updateTrainersIfNeeded(groupTraining, groupTrainingRequest);
        updateStartDateIfNeeded(groupTraining, groupTrainingRequest);
        updateEndDateIfNeeded(groupTraining, groupTrainingRequest);
        updateLocationIfNeeded(groupTraining, groupTrainingRequest);
        updateLimitIfNeeded(groupTraining, groupTrainingRequest);

        checkIfStartDateTimeIsBeforeEndDateTime(groupTraining);

        //TODO add validation LocationOccupiedException

        //TODO add validation TrainerOccupiedException

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTraining);

        //TODO send emails to all participants about changes

        return mapToGroupTrainingDTO(groupTrainingSaved);
    }

    private void updateTrainingTypeIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) throws TrainingTypeNotFoundException {
        TrainingTypeDocument trainingTypeDocument = currentGroupTraining.getTraining();
        String currentTrainingTypeId = trainingTypeDocument.getTrainingTypeId();
        String trainingTypeIdToUpdate = groupTrainingRequest.getTrainingTypeId();
        boolean trainingTypeIdIsTheSame = currentTrainingTypeId.equals(trainingTypeIdToUpdate);

        if (!trainingTypeIdIsTheSame) {
            TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeIdToUpdate);
            if (trainingType == null) throw new TrainingTypeNotFoundException();
            currentGroupTraining.setTraining(trainingType);
        }
    }

    private void updateTrainersIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) throws TrainerNotFoundException {
        List<String> currentTrainerIds = currentGroupTraining
                .getTrainers()
                .stream()
                .map(UserDocument::getUserId)
                .collect(Collectors.toList());
        List<String> trainerIds = groupTrainingRequest.getTrainerIds();

        boolean trainerIdsAreTheSame = currentTrainerIds.equals(trainerIds);

        if (!trainerIdsAreTheSame) {
            List<UserDocument> trainers = getListOfTrainersUserDocument(trainerIds);
            currentGroupTraining.setTrainers(trainers);
        }
    }

    private void updateStartDateIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) throws PastDateException {
        LocalDateTime currentStartDateTime = currentGroupTraining.getStartDate();
        String startDateTimeToUpdateStr = groupTrainingRequest.getStartDate();
        LocalDateTime startDateTimeToUpdate = LocalDateTime
                .parse(startDateTimeToUpdateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        boolean startTimeIsTheSame = currentStartDateTime.equals(startDateTimeToUpdate);

        if (!startTimeIsTheSame) {
            if (startDateTimeToUpdate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
            currentGroupTraining.setStartDate(startDateTimeToUpdate);
        }
    }

    private void updateEndDateIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) throws PastDateException {
        LocalDateTime currentEndDateTime = currentGroupTraining.getEndDate();
        String endDateTimeToUpdateStr = groupTrainingRequest.getEndDate();
        LocalDateTime endDateTimeToUpdate = LocalDateTime
                .parse(endDateTimeToUpdateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        boolean endTimeIsTheSame = currentEndDateTime.equals(endDateTimeToUpdate);

        if (!endTimeIsTheSame) {
            if (endDateTimeToUpdate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();
            currentGroupTraining.setEndDate(endDateTimeToUpdate);
        }
    }

    private void updateLocationIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) throws LocationNotFoundException {
        LocationDocument currentLocation = currentGroupTraining.getLocation();
        String currentLocationId = currentLocation.getLocationId();
        String locationIdToUpdate = groupTrainingRequest.getLocationId();
        boolean locationIdIsTheSame = currentLocationId.equals(locationIdToUpdate);

        if (!locationIdIsTheSame) {
            LocationDocument locationDocument = locationDAO.findByLocationId(locationIdToUpdate);
            if (locationDocument == null) throw new LocationNotFoundException();
            currentGroupTraining.setLocation(locationDocument);
        }
    }

    private void updateLimitIfNeeded(
            GroupTrainingDocument currentGroupTraining,
            ManagerGroupTrainingRequest groupTrainingRequest
    ) {
        int currentLimit = currentGroupTraining.getLimit();
        int limitToUpdate = groupTrainingRequest.getLimit();
        boolean limitIsTheSame = currentLimit == limitToUpdate;

        if (!limitIsTheSame) currentGroupTraining.setLimit(limitToUpdate);
    }

    private void checkIfStartDateTimeIsBeforeEndDateTime(GroupTrainingDocument groupTraining)
            throws StartDateAfterEndDateException {
        LocalDateTime startDate = groupTraining.getStartDate();
        LocalDateTime endDate = groupTraining.getEndDate();
        if (endDate.isBefore(startDate)) throw new StartDateAfterEndDateException();
    }


    @Override
    public GroupTrainingDTO removeGroupTraining(String trainingId)
            throws
            EmailSendingException,
            InvalidDateException,
            InvalidHourException,
            NotExistingGroupTrainingException {

        GroupTrainings repositoryResponse = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        if (repositoryResponse == null) throw new NotExistingGroupTrainingException();
        groupTrainingsRepository.removeByTrainingId(trainingId);

        List<UserDocument> participants = repositoryResponse.getParticipants();
        List<UserResponse> participantsResponses = new ArrayList<>();
        List<String> toEmails = new ArrayList<>();
        for (UserDocument document : participants) {
            UserResponse participantsResponse = new UserResponse(document.getUserId(),
                    document.getName(), document.getSurname());
            participantsResponses.add(participantsResponse);
            String email = document.getEmail();
            toEmails.add(email);
        }

        List<UserDocument> reserveList = repositoryResponse.getReserveList();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        for (UserDocument document : reserveList) {
            UserResponse reserveListResponse = new UserResponse(document.getUserId(),
                    document.getName(), document.getSurname());
            reserveListResponses.add(reserveListResponse);
        }

        String subject = "Training has been deleted";
        String body = "Training " + repositoryResponse.getTrainingId() + " on " + repositoryResponse.getDate() + " at "
                + repositoryResponse.getStartTime() + " with "; //TODO fix + repositoryResponse.getTrainerId() + " has been deleted.";
        try {
            emailSender.sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        //todo fix
        return new GroupTrainingDTO();

//        return new GroupTrainingResponseOld(
//                null, //TODO fix groupTrainings1.getTrainingId(),
//                null, //TODO fix groupTrainings1.getTrainingType().getName(),
//                null, //TODO fix groupTrainings1.getTrainerId(),
//                null, //TODO fix groupTrainings1.getStartDate(),
//                null, //TODO fix groupTrainings1.getEndTime(),
//                null, //TODO fix groupTrainings1.getHallNo(),
//                repositoryResponse.getLimit(),
//                INITIAL_RATING,
//                participantsResponses,
//                reserveListResponses);
    }
}
