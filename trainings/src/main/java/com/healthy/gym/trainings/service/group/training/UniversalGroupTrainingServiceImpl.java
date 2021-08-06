package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponseOld;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
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
public class UniversalGroupTrainingServiceImpl implements UniversalGroupTrainingService {

    private final TrainingTypeDAO trainingTypeRepository;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final ReviewDAO reviewDAO;
    private final Pageable paging;

    @Autowired
    public UniversalGroupTrainingServiceImpl(
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


    @Override
    public List<GroupTrainingResponseOld> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GroupTrainingResponseOld> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(
                    groupTrainingDocument.getGroupTrainingId(),
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

    @Override
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            publicResponse.add(
                    new GroupTrainingPublicResponse(
                            groupTrainingDocument.getGroupTrainingId(),
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
    public List<GroupTrainingResponseOld> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
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

        List<GroupTrainingResponseOld> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingsList) {

            GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(
                    groupTrainingDocument.getGroupTrainingId(),
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

    private List<GroupTrainingDocument> getGroupTrainingDocumentsByTrainingTypeIdBetweenStartAndEndDate(
            String trainingTypeId, String startDate, String endDate
    ) throws ParseException, StartDateAfterEndDateException {

        TrainingTypeDocument trainingTypeDocument = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);

        var dates = new DateFormatter(startDate, endDate);
        LocalDateTime dayBeforeStartDate = dates.getDayDateBeforeStartDate();
        LocalDateTime dayAfterEndDate = dates.getDayDateAfterEndDate();

        return
                groupTrainingsDAO.findByTrainingAndStartDateAfterAndEndDateBefore(
                        trainingTypeDocument, dayBeforeStartDate, dayAfterEndDate
                );
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
                    groupTrainingDocument.getGroupTrainingId(),
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


}
