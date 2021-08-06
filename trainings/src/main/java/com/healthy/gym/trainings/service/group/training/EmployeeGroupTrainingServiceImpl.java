package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeGroupTrainingServiceImpl implements EmployeeGroupTrainingService {

    private final GroupTrainingsRepository groupTrainingsRepository;
    private final GroupTrainingsDAO groupTrainingsDAO;
    private final ReviewDAO reviewDAO;
    private final Pageable paging;

    @Autowired
    public EmployeeGroupTrainingServiceImpl(
            GroupTrainingsRepository groupTrainingsRepository,
            GroupTrainingsDAO groupTrainingsDAO,
            ReviewDAO reviewDAO
    ) {
        this.groupTrainingsRepository = groupTrainingsRepository;
        this.groupTrainingsDAO = groupTrainingsDAO;
        this.reviewDAO = reviewDAO;
        this.paging = PageRequest.of(0, 1000000);
    }

    @Override
    public ParticipantsDTO getTrainingParticipants(String trainingId)
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
//
//        return participantsResponses;
//
        return new ParticipantsDTO();
    }

    private String getNotExistingGroupTrainingExceptionMessage(String trainingId) {
        return "Training with ID " + trainingId + " does not exist";
    }

    @Override
    public GroupTrainingDTO getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException,
            InvalidHourException, InvalidDateException {
        if (!groupTrainingsDAO.existsById(trainingId))
            throw new NotExistingGroupTrainingException(
                    getNotExistingGroupTrainingExceptionMessage(trainingId)
            );

        GroupTrainingDocument groupTrainingDocument = groupTrainingsDAO.findFirstByGroupTrainingId(trainingId);

        return new GroupTrainingDTO();

//        return new GroupTrainingResponseOld(
//                groupTrainingDocument.getGroupTrainingId(),
//                groupTrainingDocument.getTraining().getName(),
//                null, //TODO fix groupTrainingsDbResponse.getTrainerId(),
//                groupTrainingDocument.getStartDate(),
//                groupTrainingDocument.getEndDate(),
//                groupTrainingDocument.getLocation().getLocationId(),
//                groupTrainingDocument.getLimit(),
//                getRatingForGroupTrainings(groupTrainingDocument),
//                getBasicList(groupTrainingDocument),
//                getReserveList(groupTrainingDocument)
//        );
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
}
