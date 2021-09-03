package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.dto.GenericTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.NoTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;

import java.util.List;

public interface TrainerService {

    List<GenericTrainingDTO> getAllTrainerTrainings(String userId, String startDate, String endDate)
            throws StartDateAfterEndDateException, UserNotFoundException, NoTrainingFoundException;

}
