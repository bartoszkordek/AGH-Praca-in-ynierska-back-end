package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.AlreadyRejectedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.healthy.gym.trainings.utils.IndividualTrainingMapper.mapIndividualTrainingDocumentToDTO;

@Service
public class TrainerIndividualTrainingServiceImpl implements TrainerIndividualTrainingService {

    private final UserDAO userDAO;
    private final IndividualTrainingRepository repository;
    private final LocationDAO locationDAO;
    private final Clock clock;

    @Autowired
    public TrainerIndividualTrainingServiceImpl(
            UserDAO userDAO,
            IndividualTrainingRepository individualTrainingRepository,
            LocationDAO locationDAO,
            Clock clock
    ) {
        this.userDAO = userDAO;
        this.repository = individualTrainingRepository;
        this.locationDAO = locationDAO;
        this.clock = clock;
    }

    @Override
    public IndividualTrainingDTO acceptIndividualTraining(String userId, String trainingId, String locationId)
            throws AccessDeniedException,
            AlreadyAcceptedIndividualTrainingException,
            LocationNotFoundException,
            LocationOccupiedException,
            NotExistingIndividualTrainingException,
            PastDateException,
            UserNotFoundException {

        IndividualTrainingDocument training = getIndividualTrainingDocumentById(trainingId);

        validateIfStartTimeIsBeforeNow(training);
        getTrainerByIdAndValidateIfExistAndIsAssignToTraining(userId, training);
        validateIfAlreadyAccepted(training);

        LocationDocument location = getLocationDocumentById(locationId);
        validateIfLocationIsOccupied(training, location);

        IndividualTrainingDocument acceptedIndividualTraining =
                acceptIndividualTrainingAndSetLocationAndSave(training, location);

        sendNotification(acceptedIndividualTraining);

        return mapIndividualTrainingDocumentToDTO(acceptedIndividualTraining);
    }

    private IndividualTrainingDocument getIndividualTrainingDocumentById(String trainingId)
            throws NotExistingIndividualTrainingException {
        Optional<IndividualTrainingDocument> training = repository.findByIndividualTrainingId(trainingId);
        return training.orElseThrow(NotExistingIndividualTrainingException::new);
    }

    private void validateIfStartTimeIsBeforeNow(IndividualTrainingDocument trainingDocument)
            throws PastDateException {
        LocalDateTime startDateTime = trainingDocument.getStartDateTime();
        if (LocalDateTime.now(clock).isAfter(startDateTime)) throw new PastDateException();
    }

    private void getTrainerByIdAndValidateIfExistAndIsAssignToTraining(
            String userId,
            IndividualTrainingDocument trainingDocument
    ) throws UserNotFoundException {
        UserDocument trainer = userDAO.findByUserId(userId);
        if (trainer == null || !trainer.getGymRoles().contains(GymRole.TRAINER)) {
            throw new UserNotFoundException();
        }

        var trainers = trainingDocument.getTrainers();
        if (trainers == null || !trainers.contains(trainer)) {
            throw new AccessDeniedException("You are not allowed to accept this individual training.");
        }
    }

    private void validateIfAlreadyAccepted(IndividualTrainingDocument training)
            throws AlreadyAcceptedIndividualTrainingException {
        boolean isAccepted = training.isAccepted();
        if (isAccepted) throw new AlreadyAcceptedIndividualTrainingException();
    }

    private LocationDocument getLocationDocumentById(String locationId) throws LocationNotFoundException {
        LocationDocument location = locationDAO.findByLocationId(locationId);
        if (location == null) throw new LocationNotFoundException();
        return location;
    }

    private void validateIfLocationIsOccupied(
            IndividualTrainingDocument training,
            LocationDocument locationDocument
    ) throws LocationOccupiedException {
        //TODO locationOccupiedException
    }

    private IndividualTrainingDocument acceptIndividualTrainingAndSetLocationAndSave(
            IndividualTrainingDocument training,
            LocationDocument location
    ) {
        training.setAccepted(true);
        training.setRejected(false);
        training.setLocation(location);
        return repository.save(training);
    }

    private void sendNotification(IndividualTrainingDocument trainingDocument) {
        //TODO send email notification
    }

    @Override
    public IndividualTrainingDTO rejectIndividualTraining(String userId, String trainingId)
            throws AccessDeniedException,
            AlreadyRejectedIndividualTrainingException,
            NotExistingIndividualTrainingException,
            PastDateException,
            UserNotFoundException {

        IndividualTrainingDocument training = getIndividualTrainingDocumentById(trainingId);

        validateIfStartTimeIsBeforeNow(training);
        getTrainerByIdAndValidateIfExistAndIsAssignToTraining(userId, training);
        validateIfAlreadyRejected(training);

        IndividualTrainingDocument rejectIndividualTraining = rejectIndividualTrainingAndSave(training);

        //TODO set location to not to be occupied

        sendNotification(rejectIndividualTraining);

        return mapIndividualTrainingDocumentToDTO(rejectIndividualTraining);
    }

    private void validateIfAlreadyRejected(IndividualTrainingDocument training)
            throws AlreadyRejectedIndividualTrainingException {
        boolean isRejected = training.isRejected();
        if (isRejected) throw new AlreadyRejectedIndividualTrainingException();
    }

    private IndividualTrainingDocument rejectIndividualTrainingAndSave(IndividualTrainingDocument training) {
        training.setRejected(true);
        training.setAccepted(false);
        return repository.save(training);
    }
}
