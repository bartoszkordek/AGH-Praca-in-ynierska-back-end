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
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.exception.training.TrainingCreationException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.exception.training.TrainingUpdateException;
import com.healthy.gym.trainings.model.request.CreateGroupTrainingRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.utils.GroupTrainingMapper.mapToGroupTrainingDTO;
import static com.healthy.gym.trainings.utils.GroupTrainingValidator.*;

@Service
public class ManagerGroupTrainingServiceImpl implements ManagerGroupTrainingService {

    private static final double INITIAL_RATING = 0.0;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final LocationDAO locationDAO;
    private final UserDAO userDAO;
    private final GroupTrainingsDbRepository groupTrainingsDbRepository;
    private final EmailSender emailSender;
    private final Clock clock;

    @Autowired
    public ManagerGroupTrainingServiceImpl(
            GroupTrainingsDAO groupTrainingsDAO,
            TrainingTypeDAO trainingTypeDAO,
            LocationDAO locationDAO,
            UserDAO userDAO,
            GroupTrainingsDbRepository groupTrainingsDbRepository,
            EmailSender emailSender,
            Clock clock
    ) {
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.locationDAO = locationDAO;
        this.userDAO = userDAO;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
        this.emailSender = emailSender;
        this.clock = clock;
    }

    private List<UserResponse> getUserResponseList(List<UserDocument> usersDocuments){
        List<UserResponse> usersResponse = new ArrayList<>();
        for(UserDocument userDocument : usersDocuments){
            UserResponse userResponse = new UserResponse(
                    userDocument.getUserId(),
                    userDocument.getName(),
                    userDocument.getSurname());
            usersResponse.add(userResponse);
        }
        return usersResponse;
    }

    @Override
    public GroupTrainingDTO createGroupTraining(CreateGroupTrainingRequest createGroupTrainingRequest)
            throws StartDateAfterEndDateException, TrainerNotFoundException,
            LocationNotFoundException, TrainingTypeNotFoundException,
            LocationOccupiedException, TrainerOccupiedException, PastDateException {

        String trainingTypeId = createGroupTrainingRequest.getTrainingTypeId();
        TrainingTypeDocument trainingType = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
        if (trainingType == null) throw new TrainingTypeNotFoundException();

        List<String> trainerIds = createGroupTrainingRequest.getTrainerIds();
        List<UserDocument> trainers = new ArrayList<>();
        for (String trainerId : trainerIds) {
            UserDocument trainer = userDAO.findByUserId(trainerId);
            if (trainer == null || !trainer.getGymRoles().contains(GymRole.TRAINER))
                throw new TrainerNotFoundException();
            trainers.add(trainer);
        }

        String locationId = createGroupTrainingRequest.getLocationId();
        LocationDocument location = locationDAO.findByLocationId(locationId);
        if (location == null) throw new LocationNotFoundException();

        String startDateStr = createGroupTrainingRequest.getStartDate();
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (startDate.isBefore(LocalDateTime.now(clock))) throw new PastDateException();

        String endDateStr = createGroupTrainingRequest.getEndDate();
        LocalDateTime endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (endDate.isBefore(startDate)) throw new StartDateAfterEndDateException();

        //TODO LocationOccupiedException

        //TODO TrainerOccupiedException

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

        GroupTrainingDocument groupTrainingSaved = groupTrainingsDAO.save(groupTrainingToCreate);
        return mapToGroupTrainingDTO(groupTrainingSaved);
    }

    @Override
    public GroupTrainingResponse createGroupTraining(GroupTrainingRequest groupTrainingModel)
            throws TrainingCreationException, ParseException, InvalidHourException, InvalidDateException {

        if (!isExistRequiredDataForGroupTraining(groupTrainingModel))
            throw new TrainingCreationException("Cannot create new group training. Missing required data.");

        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();
        int hallNo = groupTrainingModel.getHallNo();
        int limit = groupTrainingModel.getLimit();

        if (isTrainingRetroDate(date))
            throw new TrainingCreationException("Cannot create new group training. Training retro date.");

        if (isStartTimeAfterEndTime(startTime, endTime))
            throw new TrainingCreationException("Cannot create new group training. Start time after end time.");

        if (isHallNoInvalid(hallNo))
            throw new TrainingCreationException("Cannot create new group training. Invalid hall no.");

        if (isLimitInvalid(limit))
            throw new TrainingCreationException("Cannot create new group training. Invalid limit.");

        if (!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModel))
            throw new TrainingCreationException("Cannot create new group training. Overlapping trainings.");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository.createTraining(groupTrainingModel);

        List<UserDocument> trainers = repositoryResponse.getParticipants();
        List<UserResponse> trainersResponses = getUserResponseList(trainers);

        List<UserDocument> participants = repositoryResponse.getParticipants();
        List<UserResponse> participantsResponses = getUserResponseList(participants);

        List<UserDocument> reserveList = repositoryResponse.getReserveList();
        List<UserResponse> reserveListResponses = getUserResponseList(reserveList);

        return new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                trainersResponses,
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                INITIAL_RATING,
                participantsResponses,
                reserveListResponses);
    }

    @Override
    public GroupTrainingResponse updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException, EmailSendingException,
            InvalidHourException, ParseException, InvalidDateException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingUpdateException("Training with ID: " + trainingId + " doesn't exist");

        String date = groupTrainingModelRequest.getDate();
        String startTime = groupTrainingModelRequest.getStartTime();
        String endTime = groupTrainingModelRequest.getEndTime();
        int hallNo = groupTrainingModelRequest.getHallNo();
        int limit = groupTrainingModelRequest.getLimit();

        if (isTrainingRetroDate(date))
            throw new TrainingUpdateException("Cannot update group training. Training retro date.");

        if (isStartTimeAfterEndTime(startTime, endTime))
            throw new TrainingUpdateException("Cannot update group training. Start time after end time.");

        if (isHallNoInvalid(hallNo))
            throw new TrainingUpdateException("Cannot update group training. Invalid hall no.");

        if (isLimitInvalid(limit))
            throw new TrainingUpdateException("Cannot update group training. Invalid limit.");

        if (!groupTrainingsDbRepository.isAbilityToUpdateTraining(trainingId, groupTrainingModelRequest))
            throw new TrainingUpdateException("Cannot update group training. Overlapping trainings.");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository
                .updateTraining(trainingId, groupTrainingModelRequest);

        List<UserDocument> trainers = repositoryResponse.getParticipants();
        List<UserResponse> trainersResponses = getUserResponseList(trainers);

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
        List<UserResponse> reserveListResponses = getUserResponseList(reserveList);

        String subject = "Training has been updated";
        String body = "Training " + repositoryResponse.getTrainingId() + " on " + repositoryResponse.getDate() + " at "
                + repositoryResponse.getStartTime() + " with " + repositoryResponse.getTrainers() + " has been updated.";
        try {
            emailSender.sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                trainersResponses,
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                INITIAL_RATING,
                participantsResponses,
                reserveListResponses);
    }

    @Override
    public GroupTrainingResponse removeGroupTraining(String trainingId)
            throws TrainingRemovalException, EmailSendingException, InvalidDateException, InvalidHourException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: " + trainingId + " doesn't exist");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository.removeTraining(trainingId);

        List<UserDocument> trainers = repositoryResponse.getParticipants();
        List<UserResponse> trainersResponses = getUserResponseList(trainers);

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
        List<UserResponse> reserveListResponses = getUserResponseList(reserveList);

        String subject = "Training has been deleted";
        String body = "Training " + repositoryResponse.getTrainingId() + " on " + repositoryResponse.getDate() + " at "
                + repositoryResponse.getStartTime() + " with " + repositoryResponse.getTrainers() + " has been deleted.";
        try {
            emailSender.sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                trainersResponses,
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                INITIAL_RATING,
                participantsResponses,
                reserveListResponses);
    }
}
