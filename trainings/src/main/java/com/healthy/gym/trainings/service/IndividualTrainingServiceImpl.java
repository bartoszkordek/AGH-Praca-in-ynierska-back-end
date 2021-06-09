package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IndividualTrainingServiceImpl implements IndividualTrainingService {

    private final EmailConfig emailConfig;
    private final IndividualTrainingsDbRepository individualTrainingsDbRepository;

    @Autowired
    public IndividualTrainingServiceImpl(
            EmailConfig emailConfig,
            IndividualTrainingsDbRepository individualTrainingsDbRepository
    ) {
        this.emailConfig = emailConfig;
        this.individualTrainingsDbRepository = individualTrainingsDbRepository;
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

    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
        String startDateAndTime = date.concat("-").concat(startDate);
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);

        Date now = new Date();

        if (requestDateParsed.before(now)) return true;

        return false;
    }

    public List<IndividualTrainings> getAllIndividualTrainings() {
        return individualTrainingsDbRepository.getIndividualTrainings();
    }

    public IndividualTrainings getIndividualTrainingById(String trainingId)
            throws NotExistingIndividualTrainingException {

        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId + " doesn't exist");
        }
        return individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
    }

    public List<IndividualTrainings> getMyAllTrainings(String clientId) {
        //add if Client Exists validation
        return individualTrainingsDbRepository.getMyAllIndividualTrainings(clientId);
    }

    public List<IndividualTrainings> getAllAcceptedIndividualTrainings() {
        return individualTrainingsDbRepository.getAcceptedIndividualTrainings();
    }

    public IndividualTrainings createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel,
            String clientId
    ) throws InvalidHourException, ParseException, RetroIndividualTrainingException {

        String individualTrainingDate = individualTrainingsRequestModel.getDate();
        String individualTrainingStartTime = individualTrainingsRequestModel.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }
        return individualTrainingsDbRepository
                .createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
    }

    public IndividualTrainings acceptIndividualTraining(
            String trainingId,
            IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel
    ) throws NotExistingIndividualTrainingException,
            AlreadyAcceptedIndividualTrainingException,
            HallNoOutOfRangeException,
            ParseException,
            RetroIndividualTrainingException,
            EmailSendingException {

        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId +
                    " doesn't exist");
        }
        if (individualTrainingsDbRepository.isIndividualTrainingExistAndAccepted(trainingId)) {
            throw new AlreadyAcceptedIndividualTrainingException("Training with ID: " + trainingId +
                    " has been already accepted");
        }
        IndividualTrainings individualTraining = individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }
        if (individualTrainingsAcceptModel.getHallNo() < 0) {
            throw new HallNoOutOfRangeException("Hall no: " + individualTrainingsAcceptModel.getHallNo() +
                    " does not exist");
        }
        IndividualTrainings response = individualTrainingsDbRepository
                .acceptIndividualTrainingRequest(trainingId, individualTrainingsAcceptModel);
        String clientId = response.getClientId();
        List<String> recipients = new ArrayList<>();
        recipients.add(clientId);
        String subject = "Training has been accepted";
        String body = "Training with" + response.getTrainerId() + " on " + response.getDate() + " at "
                + response.getStartTime() + " has been accepted.";
        try {
            sendEmailWithoutAttachment(recipients, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }
        return response;
    }

    public IndividualTrainings declineIndividualTraining(String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyDeclinedIndividualTrainingException,
            EmailSendingException {

        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId + " doesn't exist");
        }
        if (individualTrainingsDbRepository.isIndividualTrainingExistAndDeclined(trainingId)) {
            throw new AlreadyDeclinedIndividualTrainingException("Training with ID: " + trainingId +
                    " has been already declined");
        }
        IndividualTrainings response = individualTrainingsDbRepository.declineIndividualTrainingRequest(trainingId);

        String clientId = response.getClientId();
        List<String> recipients = new ArrayList<>();
        recipients.add(clientId);
        String subject = "Training has been declined";
        String body = "Training with" + response.getTrainerId() + " on " + response.getDate() + " at "
                + response.getStartTime() + " has been declined.";
        try {
            sendEmailWithoutAttachment(recipients, subject, body);
        } catch (Exception e) {
            throw new EmailSendingException("Cannot send email");
        }
        return response;
    }

    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException,
            NotAuthorizedClientException,
            ParseException,
            RetroIndividualTrainingException {

        if (!individualTrainingsDbRepository.isIndividualTrainingExist(trainingId)) {
            throw new NotExistingIndividualTrainingException("Training with ID: " + trainingId + " doesn't exist");
        }
        if (!individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(trainingId, clientId)) {
            throw new NotAuthorizedClientException("Training is not authorized by client");
        }
        IndividualTrainings individualTraining = individualTrainingsDbRepository.getIndividualTrainingById(trainingId);
        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }
        return individualTrainingsDbRepository.cancelIndividualTrainingRequest(trainingId);
    }
}
