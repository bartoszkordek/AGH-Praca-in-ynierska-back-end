package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.UserResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingsDbRepository {

    List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException;

    GroupTrainingResponse getGroupTrainingById(String trainingId)
            throws InvalidHourException, InvalidDateException;

    List<GroupTrainingResponse> getGroupTrainingsByTrainingTypeId(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException;

    List<GroupTrainingPublicResponse> getGroupTrainingsPublicByTrainingTypeId(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException;

    List<GroupTrainingPublicResponse> getMyAllGroupTrainings(String clientId)
            throws InvalidDateException, InvalidHourException;

    List<UserResponse> getTrainingParticipants(String trainingId);

    boolean isAbilityToGroupTrainingEnrollment(String trainingId);

    boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel);

    boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel);
}
