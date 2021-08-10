package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.AlreadyRejectedIndividualTrainingException;
import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import org.springframework.security.access.AccessDeniedException;

public interface TrainerIndividualTrainingService {

    IndividualTrainingDTO acceptIndividualTraining(String userId, String trainingId, String locationId)
            throws AccessDeniedException,
            AlreadyAcceptedIndividualTrainingException,
            LocationNotFoundException,
            LocationOccupiedException,
            NotExistingIndividualTrainingException,
            PastDateException,
            UserNotFoundException;

    IndividualTrainingDTO rejectIndividualTraining(String userId, String trainingId)
            throws NotExistingIndividualTrainingException,
            AlreadyRejectedIndividualTrainingException,
            PastDateException,
            EmailSendingException,
            UserNotFoundException;
}
