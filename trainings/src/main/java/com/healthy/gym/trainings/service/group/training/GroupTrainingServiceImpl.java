package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
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
    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;
    private final TrainingTypeDAO trainingTypeRepository;
    private final GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    public GroupTrainingServiceImpl(
            EmailConfig emailConfig,
            GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl,
            TrainingTypeDAO trainingTypeRepository,
            GroupTrainingsRepository groupTrainingsRepository
    ) {
        this.emailConfig = emailConfig;
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
        this.trainingTypeRepository = trainingTypeRepository;
        this.groupTrainingsRepository = groupTrainingsRepository;
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
        return groupTrainingsDbRepositoryImpl.getGroupTrainings(startDate, endDate);
    }

    @Override
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        return groupTrainingsDbRepositoryImpl.getPublicGroupTrainings(startDate, endDate);
    }

    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        return groupTrainingsDbRepositoryImpl.getGroupTrainingById(trainingId);
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
        if (!groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId)) {
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }

        return groupTrainingsDbRepositoryImpl.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate);
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
        if (!groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId)) {
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }
        return groupTrainingsDbRepositoryImpl.getGroupTrainingsPublicByTrainingTypeId(trainingTypeId, startDate, endDate);
    }

    @Override
    public List<ParticipantsResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException {

        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );
        return groupTrainingsDbRepositoryImpl.getTrainingParticipants(trainingId);
    }
}
