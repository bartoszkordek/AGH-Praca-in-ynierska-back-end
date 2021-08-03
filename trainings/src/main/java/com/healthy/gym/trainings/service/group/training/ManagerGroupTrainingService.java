package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.exception.training.TrainingUpdateException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;

import java.text.ParseException;

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
    ) throws EmailSendingException,
            InvalidDateException,
            InvalidHourException,
            LocationNotFoundException,
            LocationOccupiedException,
            NotExistingGroupTrainingException,
            ParseException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerNotFoundException,
            TrainerOccupiedException,
            TrainingTypeNotFoundException,
            TrainingUpdateException;


    GroupTrainingDTO removeGroupTraining(final String trainingId)
            throws EmailSendingException,
            InvalidDateException,
            InvalidHourException,
            NotExistingGroupTrainingException,
            TrainingRemovalException;
}
