package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;

public interface EmployeeGroupTrainingService {

    ParticipantsDTO getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException;

    GroupTrainingDTO getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException;
}
