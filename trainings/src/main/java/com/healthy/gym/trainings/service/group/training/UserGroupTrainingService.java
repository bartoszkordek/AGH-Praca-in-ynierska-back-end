package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;

import java.util.List;

public interface UserGroupTrainingService {

    List<GroupTrainingDTO> getMyAllTrainings(String clientId, String startDate, String endDate)
            throws StartDateAfterEndDateException, UserNotFoundException;

    GroupTrainingDTO enrollToGroupTraining(String trainingId, String clientId)
            throws NotExistingGroupTrainingException,
            PastDateException,
            UserAlreadyEnrolledToTrainingException,
            UserNotFoundException;

    GroupTrainingDTO removeGroupTrainingEnrollment(String trainingId, String clientId)
            throws NotExistingGroupTrainingException,
            PastDateException,
            TrainingEnrollmentException,
            UserNotFoundException;
}
