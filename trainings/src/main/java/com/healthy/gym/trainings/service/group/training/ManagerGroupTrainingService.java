package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;

public interface ManagerGroupTrainingService {

    GroupTrainingDTO createGroupTraining(final ManagerGroupTrainingRequest groupTrainingRequest)
            throws
            LocationNotFoundException,
            LocationOccupiedException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException,
            TrainingTypeNotFoundException;

    GroupTrainingDTO updateGroupTraining(
            final String trainingId,
            final ManagerGroupTrainingRequest groupTrainingRequest
    ) throws LocationNotFoundException,
            LocationOccupiedException,
            NotExistingGroupTrainingException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerNotFoundException,
            TrainerOccupiedException,
            TrainingTypeNotFoundException;

    GroupTrainingDTO removeGroupTraining(final String trainingId) throws NotExistingGroupTrainingException;
}
