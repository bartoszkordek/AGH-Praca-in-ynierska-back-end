package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
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

    @Autowired
    public GroupTrainingServiceImpl(
            EmailConfig emailConfig,
            GroupTrainingsDbRepository groupTrainingsDbRepository
    ) {
        this.emailConfig = emailConfig;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
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

    public List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {
        return groupTrainingsDbRepository.getGroupTrainings(startDate, endDate);
    }

    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        return groupTrainingsDbRepository.getPublicGroupTrainings(startDate, endDate);
    }

    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException, InvalidHourException, InvalidDateException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId) throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepository.getMyAllGroupTrainings(clientId);
    }

    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
    }

    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if (trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);

        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
    }

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

    public GroupTrainings createGroupTraining(GroupTrainingRequest groupTrainingModel)
            throws TrainingCreationException, ParseException, InvalidHourException {

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

        return groupTrainingsDbRepository.createTraining(groupTrainingModel);
    }

    public GroupTrainings removeGroupTraining(String trainingId)
            throws TrainingRemovalException, EmailSendingException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: " + trainingId + " doesn't exist");

        GroupTrainings result = groupTrainingsDbRepository.removeTraining(trainingId);

        List<String> toEmails = result.getParticipants();
        String subject = "Training has been deleted";
        String body = "Training " + result.getTrainingId() + " on " + result.getDate() + " at "
                + result.getStartTime() + " with " + result.getTrainerId() + " has been deleted.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return result;
    }

    public GroupTrainings updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest)
            throws TrainingUpdateException, EmailSendingException, InvalidHourException, ParseException {

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

        if (!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModelRequest))
            throw new TrainingUpdateException("Cannot update group training. Overlapping trainings.");

        GroupTrainings result = groupTrainingsDbRepository.updateTraining(trainingId, groupTrainingModelRequest);

        List<String> toEmails = result.getParticipants();
        String subject = "Training has been updated";
        String body = "Training " + result.getTrainingId() + " on " + result.getDate() + " at "
                + result.getStartTime() + " with " + result.getTrainerId() + " has been updated.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return result;
    }

}
