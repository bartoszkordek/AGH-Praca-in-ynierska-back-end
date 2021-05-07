package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.config.EmailConfig;
import com.healthy.gym.trainings.db.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.EmailSendModel;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TrainingsService {

    @Autowired
    EmailConfig emailConfig;

    TestRepository testRepository;
    GroupTrainingsDbRepository groupTrainingsDbRepository;
    GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository;

    public TrainingsService(TestRepository testRepository,
                            GroupTrainingsDbRepository groupTrainingsDbRepository,
                            GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository){
        this.testRepository = testRepository;
        this.groupTrainingsDbRepository = groupTrainingsDbRepository;
        this.groupTrainingReviewsDbRepository = groupTrainingReviewsDbRepository;
    }

    private void sendEmailWithoutAttachment(List<String> recipients, String subject, String body){
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

    public String getFirstTestDocument(){
        return testRepository.getFirstTestDocument();
    }

    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsDbRepository.getGroupTrainings();
    }

    public GroupTrainings getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
    }

    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
        if(trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
            throw new TrainingEnrollmentException("Cannot enroll to this training");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");

        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);

        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
    }

    public void addToReserveList(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is already enrolled to this training");
        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client already exists in reserve list");

        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
    }

    public void removeGroupTrainingEnrollment(String trainingId, String clientId) throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        if(!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
        if(!groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
           && !groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
            throw new TrainingEnrollmentException("Client is not enrolled to this training");
        if(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)){
            groupTrainingsDbRepository.removeFromParticipants(trainingId, clientId);
        }
        if(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId)){
            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
        }
    }

    public GroupTrainings createGroupTraining(GroupTrainingModel groupTrainingModel) throws TrainingCreationException, ParseException {
        if(!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModel))
            throw new TrainingCreationException("Cannot create new group training");

        return groupTrainingsDbRepository.createTraining(groupTrainingModel);
    }

    public GroupTrainings removeGroupTraining(String trainingId) throws TrainingRemovalException, EmailSendingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: "+ trainingId + " doesn't exist");

        GroupTrainings result = groupTrainingsDbRepository.removeTraining(trainingId);

        List<String> toEmails = result.getParticipants();
        String subject = "Training has been deleted";
        String body = "Training " + result.getTrainingName() + " on " + result.getDate() + " at "+result.getStartTime()+
                " with "+result.getTrainerId() + " has been deleted.";
        try{
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e){
            throw new EmailSendingException("Cannot send email");
        }


        return result;
    }

    public GroupTrainings updateGroupTraining(String trainingId, GroupTrainingModel groupTrainingModelRequest) throws TrainingUpdateException, EmailSendingException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingUpdateException("Training with ID: "+ trainingId + " doesn't exist");

        GroupTrainings result = groupTrainingsDbRepository.updateTraining(trainingId, groupTrainingModelRequest);

        List<String> toEmails = result.getParticipants();
        String subject = "Training has been updated";
        String body = "Training " + result.getTrainingName() + " on " + result.getDate() + " at "+result.getStartTime()+
                " with "+result.getTrainerId() + " has been updated.";
        try{
            sendEmailWithoutAttachment(toEmails, subject, body);
        } catch (Exception e){
            throw new EmailSendingException("Cannot send email");
        }

        return result;
    }

    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return groupTrainingReviewsDbRepository.getGroupTrainingReviews();
    }

    public GroupTrainingsReviews getGroupTrainingReviewById(String reviewId) throws NotExistingGroupTrainingReviewException {
        if(!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Review with ID: "+ reviewId + " doesn't exist");
        }
        return groupTrainingReviewsDbRepository.getGroupTrainingsReviewById(reviewId);
    }

    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingsReviewsModel groupTrainingsReviewsModel,
                                                           String clientId) throws StarsOutOfRangeException {
        if(groupTrainingsReviewsModel.getStars()<1 || groupTrainingsReviewsModel.getStars() >5){
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
        if(!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Review with ID: "+ reviewId + " doesn't exist");
        }
        if(!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)){
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        return groupTrainingReviewsDbRepository.removeGroupTrainingsReview(reviewId);
    }

    public GroupTrainingsReviews updateGroupTrainingReview(GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel,
                                                           String reviewId,
                                                           String clientId) throws NotAuthorizedClientException, StarsOutOfRangeException, NotExistingGroupTrainingReviewException {
        if(!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Review with ID: "+ reviewId + " doesn't exist");
        }
        if(!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)){
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        if(groupTrainingsReviewsUpdateModel.getStars()<1 || groupTrainingsReviewsUpdateModel.getStars() >5){
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        return groupTrainingReviewsDbRepository.updateGroupTrainingsReview(groupTrainingsReviewsUpdateModel,reviewId);
    }

}
