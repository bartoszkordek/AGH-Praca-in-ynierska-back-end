package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
public class GroupTrainingServiceImpl implements GroupTrainingService {

    private final EmailConfig emailConfig;
    private final GroupTrainingsDbRepository groupTrainingsDbRepository;
    private final TrainingTypeDAO trainingTypeRepository;

    @Autowired
    public GroupTrainingServiceImpl(
            EmailConfig emailConfig,
            GroupTrainingsDbRepository groupTrainingsDbRepository,
            TrainingTypeDAO trainingTypeRepository
    ) {
        this.emailConfig = emailConfig;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    private void sendEmailWithoutAttachment(List<String> recipients, String subject, String body) {
        String fromEmail = emailConfig.getEmailName();
        String personal = emailConfig.getEmailPersonal();
        String password = emailConfig.getEmailPassword();
        String filePath = null;
        EmailSendModel emailSendModel = new EmailSendModel(
                fromEmail,
                personal,
                recipients,
                password,
                subject,
                body,
                filePath
        );
        EmailService emailService = new EmailService();
        String host = emailConfig.getSmtpHost();
        String port = emailConfig.getSmtpPort();
        emailService.overrideDefaultSmptCredentials(host, port);
        emailService.sendEmailTLS(emailSendModel);
    }

    private boolean isExistRequiredDataForGroupTraining(GroupTrainingRequest groupTrainingModel) {
        String trainingName = groupTrainingModel.getTrainingTypeId();
        String trainerId = groupTrainingModel.getTrainerId();
        String date = groupTrainingModel.getDate();
        String startTime = groupTrainingModel.getStartTime();
        String endTime = groupTrainingModel.getEndTime();

        if (trainingName.isEmpty() || trainerId.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty())
            return false;

        return true;
    }

    private boolean isValidHallNo(int hallNo) {
        if (hallNo <= 0)
            return false;
        return true;
    }

    private boolean isValidLimit(int limit) {
        if (limit <= 0)
            return false;
        return true;
    }

    private boolean isTrainingRetroDate(String date) throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date requestDateParsed = sdfDate.parse(date);
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        Date todayDateParsed = sdfDate.parse(todayDateFormatted);

        if (requestDateParsed.before(todayDateParsed)) return true;

        return false;
    }

    private boolean isStartTimeAfterEndTime(String startTime, String endTime) {

        LocalTime start = LocalTime.parse(startTime);
        LocalTime stop = LocalTime.parse(endTime);
        Duration duration = Duration.between(start, stop);

        if (duration.toMinutes() <= 0) return true;

        return false;
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {
        return groupTrainingsDbRepository.getGroupTrainings(startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        return groupTrainingsDbRepository.getPublicGroupTrainings(startDate, endDate);
    }

    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
            throws NotExistingGroupTrainingException, InvalidHourException, StartDateAfterEndDateException,
            ParseException, InvalidDateException, TrainingTypeNotFoundException {
        if(!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)){
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }
        if(!groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)){
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }

        return groupTrainingsDbRepository.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getGroupTrainingsPublicByType(String trainingTypeId, String startDate, String endDate) throws TrainingTypeNotFoundException, NotExistingGroupTrainingException, InvalidDateException, InvalidHourException, StartDateAfterEndDateException, ParseException {
        if(!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)){
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }
        if(!groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)){
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }
        return groupTrainingsDbRepository.getGroupTrainingsPublicByTrainingTypeId(trainingTypeId, startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId) throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepository.getMyAllGroupTrainings(clientId);
    }

    @Override
    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
    }

    @Override
    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if (!groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);

        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
    }

    @Override
    public void addToReserveList(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    @Override
    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException, TrainingEnrollmentException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if (!groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
                && !groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)) {
            groupTrainingsDbRepository.removeFromParticipants(trainingId, clientId);
        }
        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId)) {
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
        }
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
        if (!isValidHallNo(hallNo))
            throw new TrainingCreationException("Cannot create new group training. Invalid hall no.");
        if (!isValidLimit(limit))
            throw new TrainingCreationException("Cannot create new group training. Invalid limit.");

        if (!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModel))
            throw new TrainingCreationException("Cannot create new group training. Overlapping trainings.");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository.createTraining(groupTrainingModel);
        GroupTrainingResponse response = new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                repositoryResponse.getTrainerId(),
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                repositoryResponse.getParticipants(),
                repositoryResponse.getReserveList());
        return response;
    }

    @Override
    public GroupTrainingResponse removeGroupTraining(String trainingId)
            throws TrainingRemovalException, EmailSendingException, InvalidDateException, InvalidHourException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: " + trainingId + " doesn't exist");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository.removeTraining(trainingId);

        List<String> toEmails = repositoryResponse.getParticipants();
        String subject = "Training has been deleted";
        String body = "Training " + repositoryResponse.getTrainingId() + " on " + repositoryResponse.getDate() + " at "
                + repositoryResponse.getStartTime() + " with " + repositoryResponse.getTrainerId() + " has been deleted.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        GroupTrainingResponse response = new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                repositoryResponse.getTrainerId(),
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                repositoryResponse.getParticipants(),
                repositoryResponse.getReserveList());
        return response;
    }

    @Override
    public GroupTrainingResponse updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException, EmailSendingException, InvalidHourException, ParseException, InvalidDateException {

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
        if (!isValidHallNo(hallNo))
            throw new TrainingUpdateException("Cannot update group training. Invalid hall no.");
        if (!isValidLimit(limit))
            throw new TrainingUpdateException("Cannot update group training. Invalid limit.");

        if (!groupTrainingsDbRepository.isAbilityToUpdateTraining(trainingId, groupTrainingModelRequest))
            throw new TrainingUpdateException("Cannot update group training. Overlapping trainings.");

        GroupTrainings repositoryResponse = groupTrainingsDbRepository.updateTraining(trainingId, groupTrainingModelRequest);

        List<String> toEmails = repositoryResponse.getParticipants();
        String subject = "Training has been updated";
        String body = "Training " + repositoryResponse.getTrainingId() + " on " + repositoryResponse.getDate() + " at "
                + repositoryResponse.getStartTime() + " with " + repositoryResponse.getTrainerId() + " has been updated.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        GroupTrainingResponse response = new GroupTrainingResponse(
                repositoryResponse.getTrainingId(),
                repositoryResponse.getTrainingType().getName(),
                repositoryResponse.getTrainerId(),
                repositoryResponse.getDate(),
                repositoryResponse.getStartTime(),
                repositoryResponse.getEndTime(),
                repositoryResponse.getHallNo(),
                repositoryResponse.getLimit(),
                repositoryResponse.getParticipants(),
                repositoryResponse.getReserveList());
        return response;
    }

}
