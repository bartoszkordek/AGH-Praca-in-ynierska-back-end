package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfiguration;
import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsRepository;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
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

    private final EmailConfiguration emailConfig;
    private final IndividualTrainingsRepository individualTrainingsRepository;
    private final UserDAO userDAO;

    @Autowired
    public IndividualTrainingServiceImpl(
            EmailConfiguration emailConfig,
            IndividualTrainingsRepository individualTrainingsRepository,
            UserDAO userDAO
    ) {
        this.emailConfig = emailConfig;
        this.individualTrainingsRepository = individualTrainingsRepository;
        this.userDAO = userDAO;
    }

    private void sendEmailWithoutAttachment(List<String> recipients, String subject, String body) {
        String fromEmail = emailConfig.getMailUsername();
        String personal = emailConfig.getEmailPersonal();
        String password = emailConfig.getMailPassword();
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
        String host = emailConfig.getMailHost();
        String port = emailConfig.getMailPort();
        emailService.overrideDefaultSmptCredentials(host, port);
        emailService.sendEmailTLS(emailSendModel);
    }

    @Override
    public List<IndividualTrainings> getAllIndividualTrainings() {
        return individualTrainingsRepository.findAll();
    }

    @Override
    public IndividualTrainings getIndividualTrainingById(String trainingId)
            throws NotExistingIndividualTrainingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);
        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        return individualTraining;
    }

    @Override
    public List<IndividualTrainings> getMyAllTrainings(String clientId) throws UserNotFoundException {
        UserDocument user = userDAO.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();
        return individualTrainingsRepository.findIndividualTrainingsByClientIdEquals(clientId);
    }

    @Override
    public List<IndividualTrainings> getAllAcceptedIndividualTrainings() {
        return individualTrainingsRepository.findAllByAccepted(true);
    }

    @Override
    public IndividualTrainings createIndividualTrainingRequest(
            IndividualTrainingRequest individualTrainingsRequestModel,
            String clientId
    ) throws InvalidHourException, ParseException, RetroIndividualTrainingException {

        String individualTrainingDate = individualTrainingsRequestModel.getDate();
        String individualTrainingStartTime = individualTrainingsRequestModel.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }

        return individualTrainingsRepository.insert(
                new IndividualTrainings(
                        clientId,
                        individualTrainingsRequestModel.getTrainerId(),
                        individualTrainingsRequestModel.getDate(),
                        individualTrainingsRequestModel.getStartTime(),
                        individualTrainingsRequestModel.getEndTime(),
                        -1,
                        individualTrainingsRequestModel.getRemarks(),
                        false,
                        false
                )
        );
    }

    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
        String startDateAndTime = date.concat("-").concat(startDate);
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);

        Date now = new Date();

        return requestDateParsed.before(now);
    }

    @Override
    public IndividualTrainings acceptIndividualTraining(
            String trainingId,
            IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel
    ) throws NotExistingIndividualTrainingException,
            AlreadyAcceptedIndividualTrainingException,
            HallNoOutOfRangeException,
            ParseException,
            RetroIndividualTrainingException,
            EmailSendingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);

        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        if (individualTraining.isAccepted()) throw new AlreadyAcceptedIndividualTrainingException();

        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();

        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }
        if (individualTrainingsAcceptModel.getHallNo() < 0) {
            throw new HallNoOutOfRangeException("Hall no: " + individualTrainingsAcceptModel.getHallNo() +
                    " does not exist");
        }

        individualTraining.setAccepted(true);
        individualTraining.setHallNo(individualTrainingsAcceptModel.getHallNo());
        IndividualTrainings response = individualTrainingsRepository.save(individualTraining);


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

    @Override
    public IndividualTrainings rejectIndividualTraining(String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyDeclinedIndividualTrainingException,
            EmailSendingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);

        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        if (individualTraining.isDeclined()) throw new AlreadyDeclinedIndividualTrainingException();

        individualTraining.setDeclined(true);
        IndividualTrainings response = individualTrainingsRepository.save(individualTraining);

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

    @Override
    public IndividualTrainings cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws NotExistingIndividualTrainingException,
            NotAuthorizedClientException,
            ParseException,
            RetroIndividualTrainingException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);

        if (individualTraining == null) throw new NotExistingIndividualTrainingException();

        boolean clientIdEquals = individualTrainingsRepository
                .existsIndividualTrainingsByIdAndClientIdEquals(trainingId, clientId);

        if (!clientIdEquals) throw new NotAuthorizedClientException("Training is not authorized by client");

        String individualTrainingDate = individualTraining.getDate();
        String individualTrainingStartTime = individualTraining.getStartTime();
        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
            throw new RetroIndividualTrainingException("Retro date");
        }

        individualTrainingsRepository.deleteIndividualTrainingsById(trainingId);
        return individualTraining;
    }
}
