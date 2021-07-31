package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
    }

    private String getNotExistingGroupTrainingExceptionMessage(String trainingId) {
        return "Training with ID " + trainingId + " does not exist";
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
            throws NotExistingGroupTrainingException, InvalidHourException, StartDateAfterEndDateException,
            ParseException, InvalidDateException, TrainingTypeNotFoundException {
        if (!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)) {
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }
        if (!groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)) {
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }

        return groupTrainingsDbRepository.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getGroupTrainingsPublicByType(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws TrainingTypeNotFoundException,
            NotExistingGroupTrainingException,
            InvalidDateException,
            InvalidHourException,
            StartDateAfterEndDateException,
            ParseException {
        if (!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)) {
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }
        if (!groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)) {
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }
        return groupTrainingsDbRepository.getGroupTrainingsPublicByTrainingTypeId(trainingTypeId, startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getMyAllTrainings(String clientId)
            throws InvalidHourException, InvalidDateException {
        //add if Client Exists validation
        return groupTrainingsDbRepository.getMyAllGroupTrainings(clientId);
    }

    @Override
    public List<ParticipantsResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException {

        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );
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
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

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
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

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
}
