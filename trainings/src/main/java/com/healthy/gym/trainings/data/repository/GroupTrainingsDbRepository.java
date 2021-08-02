package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;

import java.text.ParseException;
import java.util.List;

public interface GroupTrainingsDbRepository {

    List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate)
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException;

    List<GroupTrainingResponse> getGroupTrainingsByTrainingTypeId(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException;

    boolean isAbilityToGroupTrainingEnrollment(String trainingId);

    boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel);

    boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel);
}
