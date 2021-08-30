package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.StartEndDateNotSameDayException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;

public interface ManagerGroupTrainingService {

    GroupTrainingDTO createGroupTraining(final ManagerGroupTrainingRequest groupTrainingRequest)
            throws LocationNotFoundException,
            LocationOccupiedException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException,
            TrainingTypeNotFoundException, StartEndDateNotSameDayException;

    GroupTrainingDTO updateGroupTraining(String trainingId, final ManagerGroupTrainingRequest groupTrainingRequest)
            throws LocationNotFoundException,
            LocationOccupiedException,
            NotExistingGroupTrainingException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerNotFoundException,
            TrainerOccupiedException,
            TrainingTypeNotFoundException, StartEndDateNotSameDayException;

    GroupTrainingDTO removeGroupTraining(String trainingId) throws NotExistingGroupTrainingException;
}
