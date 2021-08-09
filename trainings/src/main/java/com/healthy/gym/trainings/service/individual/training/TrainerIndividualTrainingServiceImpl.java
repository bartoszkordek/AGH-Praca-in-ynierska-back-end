package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.configuration.EmailConfiguration;
import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.data.repository.IndividualTrainingsRepository;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TrainerIndividualTrainingServiceImpl implements TrainerIndividualTrainingService {

    private final EmailConfiguration emailConfig;
    private final IndividualTrainingsRepository individualTrainingsRepository;

    @Autowired
    public TrainerIndividualTrainingServiceImpl(
            EmailConfiguration emailConfig,
            IndividualTrainingsRepository individualTrainingsRepository
    ) {
        this.emailConfig = emailConfig;
        this.individualTrainingsRepository = individualTrainingsRepository;
    }

    @Override
    public IndividualTrainingDTO acceptIndividualTraining(String userId, String trainingId, String locationId)
            throws NotExistingIndividualTrainingException,
            AlreadyAcceptedIndividualTrainingException,
            HallNoOutOfRangeException,
            ParseException,
            RetroIndividualTrainingException,
            EmailSendingException {

//        IndividualTrainings individualTraining = individualTrainingsRepository
//                .findIndividualTrainingsById(trainingId);
//
//        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
//        if (individualTraining.isAccepted()) throw new AlreadyAcceptedIndividualTrainingException();
//
//        String individualTrainingDate = individualTraining.getDate();
//        String individualTrainingStartTime = individualTraining.getStartTime();
//
//        if (isTrainingRetroDateAndTime(individualTrainingDate, individualTrainingStartTime)) {
//            throw new RetroIndividualTrainingException("Retro date");
//        }
//        if (individualTrainingsAcceptModel.getHallNo() < 0) {
//            throw new HallNoOutOfRangeException("Hall no: " + individualTrainingsAcceptModel.getHallNo() +
//                    " does not exist");
//        }
//
//        individualTraining.setAccepted(true);
//        individualTraining.setHallNo(individualTrainingsAcceptModel.getHallNo());
//        IndividualTrainings response = individualTrainingsRepository.save(individualTraining);
//
//
//        String clientId = response.getClientId();
//        List<String> recipients = new ArrayList<>();
//        recipients.add(clientId);
//        String subject = "Training has been accepted";
//        String body = "Training with" + response.getTrainerId() + " on " + response.getDate() + " at "
//                + response.getStartTime() + " has been accepted.";
//        try {
//            sendEmailWithoutAttachment(recipients, subject, body);
//        } catch (Exception e) {
//            throw new EmailSendingException("Cannot send email");
//        }
//        //return response;
        return null;
    }

    private boolean isTrainingRetroDateAndTime(String date, String startDate) throws ParseException {
        String startDateAndTime = date.concat("-").concat(startDate);
        SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Date requestDateParsed = sdfDateAndTime.parse(startDateAndTime);

        Date now = new Date();

        return requestDateParsed.before(now);
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
    public IndividualTrainingDTO rejectIndividualTraining(String userId, String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyRejectedIndividualTrainingException,
            EmailSendingException, PastDateException {

        IndividualTrainings individualTraining = individualTrainingsRepository
                .findIndividualTrainingsById(trainingId);

        if (individualTraining == null) throw new NotExistingIndividualTrainingException();
        if (individualTraining.isDeclined()) throw new AlreadyRejectedIndividualTrainingException();

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
//        return response;
        return null;
    }
}
