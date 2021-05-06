package com.healthy.gym.trainings.service;

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
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TrainingsService {
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

    public GroupTrainings removeGroupTraining(String trainingId) throws TrainingRemovalException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingRemovalException("Training with ID: "+ trainingId + " doesn't exist");

        String fromEmail = "silownia_herkules@vp.pl";
        String personal = "Siłownia Herkules";
        String toEmail = "test_client@vp.pl";
        String password = "test_password123";
        String subject = "Test Message";
        String body = "This is test message";
        String filePath = null;
        EmailSendModel emailSendModel = new EmailSendModel(fromEmail, personal, toEmail, password, subject, body, filePath);
        EmailService emailService = new EmailService();
        emailService.sendEmailTLS(emailSendModel);
        return groupTrainingsDbRepository.removeTraining(trainingId);
    }

    public GroupTrainings updateGroupTraining(String trainingId, GroupTrainingModel groupTrainingModelRequest) throws TrainingUpdateException {
        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new TrainingUpdateException("Training with ID: "+ trainingId + " doesn't exist");
        return groupTrainingsDbRepository.updateTraining(trainingId, groupTrainingModelRequest);
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
