package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.utils.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getBasicList;
import static com.healthy.gym.trainings.utils.ParticipantsExtractor.getReserveList;

@Service
public class GroupTrainingServiceImpl implements GroupTrainingService {

    private final TrainingTypeDAO trainingTypeRepository;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final ReviewDAO reviewDAO;
    private final Pageable paging;

    @Autowired
    public GroupTrainingServiceImpl(
            TrainingTypeDAO trainingTypeRepository,
            GroupTrainingsRepository groupTrainingsRepository,
            GroupTrainingsDAO groupTrainingsDAO,
            TrainingTypeDAO trainingTypeDAO,
            ReviewDAO reviewDAO
    ) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.trainingTypeDAO = trainingTypeDAO;
        this.reviewDAO = reviewDAO;
        this.paging = PageRequest.of(0, 1000000);
    }

    private double getRatingForGroupTrainings(GroupTrainingDocument groupTraining) {
        List<GroupTrainingReviewResponse> groupTrainingsReviews = reviewDAO
                .findByDateBetweenAndTrainingTypeId(
                        null,
                        null,
                        groupTraining.getTraining().getTrainingTypeId(),
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

    private double getRatingForGroupTrainingList(@NotNull List<GroupTrainingDocument> groupTrainingsList) {
        double rating = 0.0;
        if (!groupTrainingsList.isEmpty()) {
            List<GroupTrainingReviewResponse> groupTrainingsReviews = reviewDAO
                    .findByDateBetweenAndTrainingTypeId(
                            null,
                            null,
                            groupTrainingsList.get(0).getTraining().getTrainingTypeId(),
                            paging
                    ).getContent();

            double sum = 0;
            int counter = 0;
            for (GroupTrainingReviewResponse review : groupTrainingsReviews) {
                sum += review.getStars();
                counter++;
            }
            if (counter != 0) rating = sum / counter;
        }
        return rating;
    }

    private String getNotExistingGroupTrainingExceptionMessage(String trainingId) {
        return "Training with ID " + trainingId + " does not exist";
    }

    private List<GroupTrainingDocument> getGroupTrainingDocumentsBetweenStartAndEndDate(
            String startDate, String endDate) throws ParseException, StartDateAfterEndDateException {
        var dates = new DateFormatter(startDate, endDate);
        LocalDateTime dayBeforeStartDate = dates.getDayDateBeforeStartDate();
        LocalDateTime dayAfterEndDate = dates.getDayDateAfterEndDate();

        return groupTrainingsDAO
                .findByStartDateAfterAndEndDateBefore(
                        dayBeforeStartDate,
                        dayAfterEndDate);
    }

    List<GroupTrainingDocument> getGroupTrainingDocumentsByTrainingTypeIdBetweenStartAndEndDate(
            String trainingTypeId, String startDate, String endDate) throws ParseException, StartDateAfterEndDateException {
        TrainingTypeDocument trainingTypeDocument = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);

        var dates = new DateFormatter(startDate, endDate);
        LocalDateTime dayBeforeStartDate = dates.getDayDateBeforeStartDate();
        LocalDateTime dayAfterEndDate = dates.getDayDateAfterEndDate();

        return
                groupTrainingsDAO.findByTrainingAndStartDateAfterAndEndDateBefore(
                        trainingTypeDocument, dayBeforeStartDate, dayAfterEndDate
                );
    }



    @Override
    public List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GroupTrainingResponse> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(
                    groupTrainingDocument.getId(),
                    groupTrainingDocument.getTraining().getName(),
                    null, //TODO fix training.getTrainerId(),
                    groupTrainingDocument.getStartDate(),
                    groupTrainingDocument.getEndDate(),
                    groupTrainingDocument.getLocation().getLocationId(),
                    groupTrainingDocument.getLimit(),
                    getRatingForGroupTrainings(groupTrainingDocument),
                    getBasicList(groupTrainingDocument),
                    getReserveList(groupTrainingDocument)
            );
            result.add(groupTraining);
        }
        return result;
    }

    @Override
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            publicResponse.add(
                    new GroupTrainingPublicResponse(
                            groupTrainingDocument.getId(),
                            groupTrainingDocument.getTraining().getName(),
                            null, //TODO fix groupTraining.getTrainerId(),
                            groupTrainingDocument.getStartDate(),
                            groupTrainingDocument.getEndDate(),
                            groupTrainingDocument.getLocation().getLocationId(),
                            groupTrainingDocument.getLimit(),
                            getRatingForGroupTrainings(groupTrainingDocument)
                    )
            );
        }

        return publicResponse;
    }

    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsDAO.existsById(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        GroupTrainingDocument groupTrainingDocument = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);

        return new GroupTrainingResponse(
                groupTrainingDocument.getId(),
                groupTrainingDocument.getTraining().getName(),
                null, //TODO fix groupTrainingsDbResponse.getTrainerId(),
                groupTrainingDocument.getStartDate(),
                groupTrainingDocument.getEndDate(),
                groupTrainingDocument.getLocation().getLocationId(),
                groupTrainingDocument.getLimit(),
                getRatingForGroupTrainings(groupTrainingDocument),
                getBasicList(groupTrainingDocument),
                getReserveList(groupTrainingDocument)
        );
    }

    @Override
    public List<GroupTrainingResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
            throws NotExistingGroupTrainingException, InvalidHourException, StartDateAfterEndDateException,
            ParseException, InvalidDateException, TrainingTypeNotFoundException {
        if (!trainingTypeDAO.existsByTrainingTypeId(trainingTypeId)) {
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }
        if (!groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId)) {
            throw new NotExistingGroupTrainingException("Trainings with type ID " + trainingTypeId + " does not exist");
        }

        List<GroupTrainingDocument> groupTrainingsList =
                getGroupTrainingDocumentsByTrainingTypeIdBetweenStartAndEndDate(
                        trainingTypeId, startDate, endDate);


        double rating = getRatingForGroupTrainingList(groupTrainingsList);

        List<GroupTrainingResponse> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingsList) {

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(
                    groupTrainingDocument.getId(),
                    groupTrainingDocument.getTraining().getName(),
                    null, //TODO fix training.getTrainerId(),
                    groupTrainingDocument.getStartDate(),
                    groupTrainingDocument.getEndDate(),
                    groupTrainingDocument.getLocation().getLocationId(),
                    groupTrainingDocument.getLimit(),
                    rating,
                    getBasicList(groupTrainingDocument),
                    getReserveList(groupTrainingDocument)
            );
            result.add(groupTraining);
        }
        return result;
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

        List<GroupTrainingDocument> groupTrainingsList =
                getGroupTrainingsByTrainingTypeIdAndDates(trainingTypeId, startDate, endDate);
        double rating = getRatingForGroupTrainingList(groupTrainingsList);

        List<GroupTrainingPublicResponse> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingsList) {
            GroupTrainingPublicResponse groupTraining = new GroupTrainingPublicResponse(
                    groupTrainingDocument.getId(),
                    groupTrainingDocument.getTraining().getName(),
                    null, //TODO fix training.getTrainerId(),
                    groupTrainingDocument.getStartDate(),
                    groupTrainingDocument.getEndDate(),
                    groupTrainingDocument.getLocation().getLocationId(),
                    groupTrainingDocument.getLimit(),
                    rating
            );
            result.add(groupTraining);
        }
        return result;
    }

    private List<GroupTrainingDocument> getGroupTrainingsByTrainingTypeIdAndDates(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException {

        var dates = new DateFormatter(startDate, endDate);
        LocalDateTime dayBeforeStartDate = dates.getDayDateBeforeStartDate();
        LocalDateTime dayAfterEndDate = dates.getDayDateAfterEndDate();

        TrainingTypeDocument trainingTypeDocument = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);

        return groupTrainingsDAO.findByTrainingAndStartDateAfterAndEndDateBefore(
                        trainingTypeDocument,
                        dayBeforeStartDate,
                        dayAfterEndDate
                );
    }


    @Override
    public List<UserResponse> getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException {

        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserDocument> participants = groupTrainingsRepository
                .getFirstByTrainingId(trainingId)
                .getParticipants();

        for (UserDocument userDocument : participants) {
            UserResponse participantsResponse = new UserResponse(
                    userDocument.getUserId(),
                    userDocument.getName(),
                    userDocument.getSurname()
            );
            participantsResponses.add(participantsResponse);
        }

        return participantsResponses;
    }
}
