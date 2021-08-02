package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.service.email.EmailService;
import com.healthy.gym.trainings.utils.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getBasicList;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getReserveList;

@Service
public class GroupTrainingServiceImpl implements GroupTrainingService {

    private final EmailConfig emailConfig;
    private final GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl;
    private final TrainingTypeDAO trainingTypeRepository;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final ReviewDAO reviewDAO;
    private final Pageable paging;

    @Autowired
    public GroupTrainingServiceImpl(
            EmailConfig emailConfig,
            GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl,
            TrainingTypeDAO trainingTypeRepository,
            GroupTrainingsRepository groupTrainingsRepository,
            ReviewDAO reviewDAO
    ) {
        this.emailConfig = emailConfig;
        this.groupTrainingsDbRepositoryImpl = groupTrainingsDbRepositoryImpl;
        this.trainingTypeRepository = trainingTypeRepository;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.reviewDAO = reviewDAO;
        this.paging = PageRequest.of(0, 1000000);
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

        var dates = new DateFormatter(startDate, endDate);
        String dayBeforeStartDate = dates.getFormattedDayDateBeforeStartDate();
        String dayAfterEndDate = dates.getFormattedDayDateAfterEndDate();

        List<GroupTrainings> groupTrainings = groupTrainingsRepository
                .findByDateBetween(dayBeforeStartDate, dayAfterEndDate);

        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        for (GroupTrainings groupTraining : groupTrainings) {

            publicResponse.add(
                    new GroupTrainingPublicResponse(
                            groupTraining.getTrainingId(),
                            groupTraining.getTrainingType().getName(),
                            null, //TODO fix groupTraining.getTrainerId(),
                            groupTraining.getDate(),
                            groupTraining.getStartTime(),
                            groupTraining.getEndTime(),
                            groupTraining.getHallNo(),
                            groupTraining.getLimit(),
                            getRatingForGroupTrainings(groupTraining)
                    )
            );
        }

        return publicResponse;
    }

    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        GroupTrainings groupTrainingsDbResponse = groupTrainingsRepository.findFirstByTrainingId(trainingId);

        return new GroupTrainingResponse(
                groupTrainingsDbResponse.getTrainingId(),
                groupTrainingsDbResponse.getTrainingType().getName(),
                null, //TODO fix groupTrainingsDbResponse.getTrainerId(),
                groupTrainingsDbResponse.getDate(),
                groupTrainingsDbResponse.getStartTime(),
                groupTrainingsDbResponse.getEndTime(),
                groupTrainingsDbResponse.getHallNo(),
                groupTrainingsDbResponse.getLimit(),
                getRatingForGroupTrainings(groupTrainingsDbResponse),
                getBasicList(groupTrainingsDbResponse),
                getReserveList(groupTrainingsDbResponse)
        );
    }

    private double getRatingForGroupTrainings(GroupTrainings groupTraining) {
        List<GroupTrainingReviewResponse> groupTrainingsReviews = reviewDAO
                .findByDateBetweenAndTrainingTypeId(
                        null,
                        null,
                        groupTraining.getTrainingType().getTrainingTypeId(),
                        paging
                ).getContent();

        double rating = 0.0;
        double sum = 0;
        int counter = 0;
        for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
            sum += review.getStars();
            counter++;
        }
        if (counter != 0) rating = sum / counter;

        return rating;
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
    public List<UserResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException {

        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );
        return groupTrainingsDbRepositoryImpl.getTrainingParticipants(trainingId);
    }
}
