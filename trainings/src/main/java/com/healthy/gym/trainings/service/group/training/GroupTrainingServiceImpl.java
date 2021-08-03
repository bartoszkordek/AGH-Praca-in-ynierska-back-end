package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
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
import com.healthy.gym.trainings.model.response.*;
import com.healthy.gym.trainings.shared.*;
import com.healthy.gym.trainings.utils.DateFormatter;
import com.healthy.gym.trainings.utils.DateValidator;
import com.healthy.gym.trainings.utils.Time24HoursValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupTrainingServiceImpl implements GroupTrainingService {

    private final TrainingTypeDAO trainingTypeRepository;
    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final ReviewDAO reviewDAO;
    private final Pageable paging;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;

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

        dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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

    private List<GroupTrainingDocument> getGroupTrainingDocumentsByTrainingTypeIdBetweenStartAndEndDate(
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

    private List<BasicUserInfoDTO> mapUserDocumentsToBasicUserInfoDTO(List<UserDocument> userDocuments){

        List<BasicUserInfoDTO> basicUserInfoDTOs = new ArrayList<>();
        for(UserDocument userDocument : userDocuments){
            BasicUserInfoDTO basicUserInfoDTO = new BasicUserInfoDTO(
                    userDocument.getUserId(),
                    userDocument.getName(),
                    userDocument.getSurname(),
                    null//TODO getAvatarUrl
            );
            basicUserInfoDTOs.add(basicUserInfoDTO);
        }
        return basicUserInfoDTOs;
    }



    @Override
    public GroupTrainingsResponse getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GetGroupTrainingDTO> groupTrainingsDTO = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            LocalDateTime documentStartDate = groupTrainingDocument.getStartDate();
            LocalDateTime documentEndDate = groupTrainingDocument.getEndDate();

            if (!DateValidator.validate(documentStartDate.format(dateFormatter))
                    || !Time24HoursValidator.validate(documentStartDate.format(timeFormatter)))
                throw new InvalidDateException("Wrong start date or time");

            if (!DateValidator.validate(documentEndDate.format(dateFormatter))
                    || !Time24HoursValidator.validate(documentEndDate.format(timeFormatter)))
                throw new InvalidHourException("Wrong end date or time");

            double rating = getRatingForGroupTrainings(groupTrainingDocument);

            List<BasicUserInfoDTO> trainersDTO = new ArrayList<>();
            List<UserDocument> trainersDocuments = groupTrainingDocument.getTrainers();
            trainersDTO = mapUserDocumentsToBasicUserInfoDTO(trainersDocuments);

            //PARTICIPANTS
            List<BasicUserInfoDTO> basicListDTO = new ArrayList<>();
            List<UserDocument> basicListDocuments = groupTrainingDocument.getBasicList();
            basicListDTO = mapUserDocumentsToBasicUserInfoDTO(basicListDocuments);

            List<BasicUserInfoDTO> reserveListDTO = new ArrayList<>();
            List<UserDocument> reserveListDocuments = groupTrainingDocument.getReserveList();
            reserveListDTO = mapUserDocumentsToBasicUserInfoDTO(reserveListDocuments);

            ParticipantsDTO participantsDTO = new ParticipantsDTO(basicListDTO, reserveListDTO);

            GetGroupTrainingDTO groupTrainingDTO = new GetGroupTrainingDTO(
                    groupTrainingDocument.getGroupTrainingId(),
                    groupTrainingDocument.getTraining().getName(),
                    documentStartDate.format(dateFormatter).concat("T").concat(documentStartDate.format(timeFormatter)),
                    documentEndDate.format(dateFormatter).concat("T").concat(documentEndDate.format(timeFormatter)),
                    false,
                    groupTrainingDocument.getLocation().getLocationId(),
                    rating,
                    trainersDTO,
                    participantsDTO
            );

            groupTrainingsDTO.add(groupTrainingDTO);
        }
        return new GroupTrainingsResponse(groupTrainingsDTO);
    }

    @Override
    public GroupTrainingsPublicResponse getPublicGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {

        List<GroupTrainingDocument> groupTrainingDocuments = getGroupTrainingDocumentsBetweenStartAndEndDate(
                startDate, endDate);

        List<GetGroupTrainingPublicDTO> groupTrainingsDTO = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingDocuments) {

            LocalDateTime documentStartDate = groupTrainingDocument.getStartDate();
            LocalDateTime documentEndDate = groupTrainingDocument.getEndDate();

            if (!DateValidator.validate(documentStartDate.format(dateFormatter))
                    || !Time24HoursValidator.validate(documentStartDate.format(timeFormatter)))
                throw new InvalidDateException("Wrong start date or time");

            if (!DateValidator.validate(documentEndDate.format(dateFormatter))
                    || !Time24HoursValidator.validate(documentEndDate.format(timeFormatter)))
                throw new InvalidHourException("Wrong end date or time");

            double rating = getRatingForGroupTrainings(groupTrainingDocument);

            List<BasicUserInfoDTO> trainersDTO = new ArrayList<>();
            List<UserDocument> trainersDocuments = groupTrainingDocument.getTrainers();
            trainersDTO = mapUserDocumentsToBasicUserInfoDTO(trainersDocuments);

            GetGroupTrainingPublicDTO groupTrainingDTO = new GetGroupTrainingPublicDTO(
                    groupTrainingDocument.getGroupTrainingId(),
                    groupTrainingDocument.getTraining().getName(),
                    documentStartDate.format(dateFormatter).concat("T").concat(documentStartDate.format(timeFormatter)),
                    documentEndDate.format(dateFormatter).concat("T").concat(documentEndDate.format(timeFormatter)),
                    false,
                    groupTrainingDocument.getLocation().getLocationId(),
                    rating,
                    trainersDTO
            );

            groupTrainingsDTO.add(groupTrainingDTO);
        }

        return new GroupTrainingsPublicResponse(groupTrainingsDTO);
    }

    @Override
    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsDAO.existsById(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        GroupTrainingDocument groupTrainingDocument = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);

        LocalDateTime documentStartDate = groupTrainingDocument.getStartDate();
        LocalDateTime documentEndDate = groupTrainingDocument.getEndDate();

        if (!DateValidator.validate(documentStartDate.format(dateFormatter))
                || !Time24HoursValidator.validate(documentStartDate.format(timeFormatter)))
            throw new InvalidDateException("Wrong start date or time");

        if (!DateValidator.validate(documentEndDate.format(dateFormatter))
                || !Time24HoursValidator.validate(documentEndDate.format(timeFormatter)))
            throw new InvalidHourException("Wrong end date or time");

        double rating = getRatingForGroupTrainings(groupTrainingDocument);

        List<BasicUserInfoDTO> trainersDTO = new ArrayList<>();
        List<UserDocument> trainersDocuments = groupTrainingDocument.getTrainers();
        trainersDTO = mapUserDocumentsToBasicUserInfoDTO(trainersDocuments);

        //PARTICIPANTS
        List<BasicUserInfoDTO> basicListDTO = new ArrayList<>();
        List<UserDocument> basicListDocuments = groupTrainingDocument.getBasicList();
        basicListDTO = mapUserDocumentsToBasicUserInfoDTO(basicListDocuments);

        List<BasicUserInfoDTO> reserveListDTO = new ArrayList<>();
        List<UserDocument> reserveListDocuments = groupTrainingDocument.getReserveList();
        reserveListDTO = mapUserDocumentsToBasicUserInfoDTO(reserveListDocuments);

        ParticipantsDTO participantsDTO = new ParticipantsDTO(basicListDTO, reserveListDTO);

        GetGroupTrainingDTO groupTrainingDTO = new GetGroupTrainingDTO(
                groupTrainingDocument.getGroupTrainingId(),
                groupTrainingDocument.getTraining().getName(),
                documentStartDate.format(dateFormatter).concat("T").concat(documentStartDate.format(timeFormatter)),
                documentEndDate.format(dateFormatter).concat("T").concat(documentEndDate.format(timeFormatter)),
                false,
                groupTrainingDocument.getLocation().getLocationId(),
                rating,
                trainersDTO,
                participantsDTO
        );

        return new GroupTrainingResponse(groupTrainingDTO);
    }

    //TEMPORARY commented
    /*@Override
    public List<GroupTrainingsResponse> getGroupTrainingsByType(String trainingTypeId, String startDate, String endDate)
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

        List<GroupTrainingsResponse> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingsList) {

            GroupTrainingsResponse groupTraining = new GroupTrainingsResponse(
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

    @Override
    public List<GroupTrainingsPublicResponse> getGroupTrainingsPublicByType(
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

        List<GroupTrainingsPublicResponse> result = new ArrayList<>();
        for (GroupTrainingDocument groupTrainingDocument : groupTrainingsList) {
            GroupTrainingsPublicResponse groupTraining = new GroupTrainingsPublicResponse(
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
    }*/

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
    public GroupTrainingParticipantsResponse getTrainingParticipants(String trainingId)
            throws NotExistingGroupTrainingException {

        if (!groupTrainingsRepository.existsByTrainingId(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        List<BasicUserInfoDTO> participantsResponses = new ArrayList<>();
        List<UserDocument> participants = groupTrainingsRepository
                .getFirstByTrainingId(trainingId)
                .getParticipants();

        for (UserDocument userDocument : participants) {
            BasicUserInfoDTO participantsResponse = new BasicUserInfoDTO(
                    userDocument.getUserId(),
                    userDocument.getName(),
                    userDocument.getSurname(),
                    null //TODO add avatar
            );
            participantsResponses.add(participantsResponse);
        }

        return new GroupTrainingParticipantsResponse(participantsResponses);
    }
}
