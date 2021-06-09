package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
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
public class GroupTrainingsService {

    @Autowired
    EmailConfig emailConfig;
    GroupTrainingsDbRepository groupTrainingsDbRepository;
    GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository;

    public GroupTrainingsService(
            GroupTrainingsDbRepository groupTrainingsDbRepository,
            GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository
    ) {
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
        this.groupTrainingReviewsDbRepository = groupTrainingReviewsDbRepository;
    }

    private void sendEmailWithoutAttachment(List<String> recipients, String subject, String body) {
        String fromEmail = emailConfig.getEmailName();
        String personal = emailConfig.getEmailPersonal();
        String password = emailConfig.getEmailPassword();
        String filePath = null;
        EmailSendModel emailSendModel = new EmailSendModel(fromEmail, personal, recipients, password, subject, body, filePath);
        EmailService emailService = new EmailService();
        String host = emailConfig.getSmtpHost();
        String port = emailConfig.getSmtpPort();
        emailService.overrideDefaultSmptCredentials(host, port);
        emailService.sendEmailTLS(emailSendModel);
    }

    private boolean isExistRequiredDataForGroupTraining(GroupTrainingRequest groupTrainingModel) {
        String trainingName = groupTrainingModel.getTrainingName();
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

    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsDbRepository.getGroupTrainings();
    }

    public List<GroupTrainingPublicResponse> getPublicGroupTrainings() throws InvalidHourException, InvalidDateException {
        return groupTrainingsDbRepository.getPublicGroupTrainings();
    }

    public GroupTrainings getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    public List<GroupTrainings> getMyAllTrainings(String clientId) {
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

    public void addToReserveList(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    public void removeGroupTrainingEnrollment(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
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

    public GroupTrainings createGroupTraining(GroupTrainingRequest groupTrainingModel) throws TrainingCreationException, ParseException, InvalidHourException {
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

    public GroupTrainings removeGroupTraining(String trainingId) throws TrainingRemovalException, EmailSendingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: " + trainingId + " doesn't exist");

        GroupTrainings result = groupTrainingsDbRepository.removeTraining(trainingId);

        List<String> toEmails = result.getParticipants();
        String subject = "Training has been deleted";
        String body = "Training " + result.getTrainingName() + " on " + result.getDate() + " at " + result.getStartTime() +
                " with " + result.getTrainerId() + " has been deleted.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return result;
    }

    public GroupTrainings updateGroupTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest) throws TrainingUpdateException, EmailSendingException, InvalidHourException, ParseException {
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
        String body = "Training " + result.getTrainingName() + " on " + result.getDate() + " at " + result.getStartTime() +
                " with " + result.getTrainerId() + " has been updated.";
        try {
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }

        return result;
    }

    public List<GroupTrainingsReviews> getGroupTrainingReviews() {
        return groupTrainingReviewsDbRepository.getGroupTrainingReviews();
    }

    public GroupTrainingsReviews getGroupTrainingReviewById(String reviewId) throws NotExistingGroupTrainingReviewException {
        if (!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)) {
            throw new NotExistingGroupTrainingReviewException("Review with ID: " + reviewId + " doesn't exist");
        }
        return groupTrainingReviewsDbRepository.getGroupTrainingsReviewById(reviewId);
    }

    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviewsModel,
                                                           String clientId) throws StarsOutOfRangeException {
        if (groupTrainingsReviewsModel.getStars() < 1 || groupTrainingsReviewsModel.getStars() > 5) {
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        return groupTrainingReviewsDbRepository.createGroupTrainingReview(groupTrainingsReviewsModel,
                todayDateFormatted,
                clientId);
    }

    public GroupTrainingsReviews removeGroupTrainingReview(String reviewId, String clientId) throws NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        if (!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)) {
            throw new NotExistingGroupTrainingReviewException("Review with ID: " + reviewId + " doesn't exist");
        }
        if (!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)) {
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        return groupTrainingReviewsDbRepository.removeGroupTrainingsReview(reviewId);
    }

    public GroupTrainingsReviews updateGroupTrainingReview(GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
                                                           String reviewId,
                                                           String clientId) throws NotAuthorizedClientException, StarsOutOfRangeException, NotExistingGroupTrainingReviewException {
        if (!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)) {
            throw new NotExistingGroupTrainingReviewException("Review with ID: " + reviewId + " doesn't exist");
        }
        if (!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)) {
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        if (groupTrainingsReviewsUpdateModel.getStars() < 1 || groupTrainingsReviewsUpdateModel.getStars() > 5) {
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        return groupTrainingReviewsDbRepository.updateGroupTrainingsReview(groupTrainingsReviewsUpdateModel, reviewId);
    }

}
