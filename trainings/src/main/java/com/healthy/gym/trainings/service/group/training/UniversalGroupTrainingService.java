package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;

import java.util.List;

public interface UniversalGroupTrainingService {

    List<GroupTrainingDTO> getGroupTrainingsWithParticipants(String startDate, String endDate)
            throws StartDateAfterEndDateException;

    List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsWithoutParticipants(String startDate, String endDate)
            throws StartDateAfterEndDateException;

    List<GroupTrainingDTO> getGroupTrainingsByTypeWithParticipants(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws StartDateAfterEndDateException, TrainingTypeNotFoundException;

    List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsByTypeWithoutParticipants(
            String trainingTypeId,
            String startDate,
            String endDate
    ) throws StartDateAfterEndDateException, TrainingTypeNotFoundException;
}
