package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.data.repository.individual.training.UserIndividualTrainingDAO;
import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidTrainerSpecifiedException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import com.healthy.gym.trainings.utils.CollisionValidator;
import com.healthy.gym.trainings.component.CollisionValidatorComponent;
import com.healthy.gym.trainings.utils.IndividualTrainingMapper;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.utils.DateParser.parseDateTime;
import static com.healthy.gym.trainings.utils.IndividualTrainingMapper.mapIndividualTrainingDocumentToDTO;
import static com.healthy.gym.trainings.utils.IndividualTrainingMapper.mapGroupTrainingToBasicTrainingDTO;

@Service
public class UserIndividualTrainingServiceImpl implements UserIndividualTrainingService {

    private final CollisionValidatorComponent collisionValidator;
    private final IndividualTrainingRepository individualTrainingRepository;
    private final UserIndividualTrainingDAO userIndividualTrainingDAO;
    private final UserDAO userDAO;
    private final Clock clock;

    @Autowired
    public UserIndividualTrainingServiceImpl(
            CollisionValidatorComponent collisionValidator,
            IndividualTrainingRepository individualTrainingRepository,
            UserIndividualTrainingDAO userIndividualTrainingDAO,
            UserDAO userDAO,
            Clock clock
    ) {
        this.collisionValidator = collisionValidator;
        this.individualTrainingRepository = individualTrainingRepository;
        this.userIndividualTrainingDAO = userIndividualTrainingDAO;
        this.userDAO = userDAO;
        this.clock = clock;
    }

    @Override
    public List<IndividualTrainingDTO> getMyAllTrainings(String clientId, String startDate, String endDate)
            throws UserNotFoundException, StartDateAfterEndDateException, NoIndividualTrainingFoundException {

        StartEndDateValidator validator = new StartEndDateValidator(startDate, endDate);
        LocalDateTime startDateTime = validator.getBeginningOfStartDate();
        LocalDateTime endDateTime = validator.getEndOfEndDate();

        UserDocument user = getAndValidateUser(clientId);
        List<IndividualTrainingDocument> trainingDocumentList = userIndividualTrainingDAO
                .findAllIndividualTrainingsWithDatesByUserDocument(user, startDateTime, endDateTime);

        if (trainingDocumentList.isEmpty()) throw new NoIndividualTrainingFoundException();

        return trainingDocumentList
                .stream()
                .map(IndividualTrainingMapper::mapIndividualTrainingDocumentToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IndividualTrainingDTO createIndividualTrainingRequest(
            final IndividualTrainingRequest individualTrainingsRequestModel,
            final String clientId
    ) throws InvalidTrainerSpecifiedException,
            PastDateException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException,
            UserNotFoundException {

        UserDocument user = getAndValidateUser(clientId);
        UserDocument trainer = getAndValidateTrainer(individualTrainingsRequestModel);

        LocalDateTime[] localDateTimes =
                getAndValidateStartDateTimeAndEndDateTime(individualTrainingsRequestModel);
        LocalDateTime startDateTime = localDateTimes[0];
        LocalDateTime endDateTime = localDateTimes[1];

        String remarks = individualTrainingsRequestModel.getRemarks();
        validateIfTrainerIsOccupied(startDateTime, endDateTime, trainer);

        IndividualTrainingDocument trainingDocument = new IndividualTrainingDocument(
                UUID.randomUUID().toString(),
                null,
                List.of(user),
                List.of(trainer),
                startDateTime,
                endDateTime,
                null,
                remarks
        );

        IndividualTrainingDocument createdTrainingRequest = individualTrainingRepository.save(trainingDocument);
        return mapIndividualTrainingDocumentToDTO(createdTrainingRequest);
    }

    private UserDocument getAndValidateUser(String userId) throws UserNotFoundException {
        UserDocument user = userDAO.findByUserId(userId);
        if (user == null) throw new UserNotFoundException();
        return user;
    }

    private UserDocument getAndValidateTrainer(IndividualTrainingRequest individualTrainingsRequestModel)
            throws TrainerNotFoundException, InvalidTrainerSpecifiedException {
        String trainerId = individualTrainingsRequestModel.getTrainerId();
        UserDocument trainer = userDAO.findByUserId(trainerId);
        if (trainer == null) throw new TrainerNotFoundException();

        Collection<GymRole> trainerRoles = trainer.getGymRoles();
        if (!trainerRoles.contains(GymRole.TRAINER)) {
            throw new InvalidTrainerSpecifiedException();
        }
        return trainer;
    }

    private LocalDateTime[] getAndValidateStartDateTimeAndEndDateTime(
            IndividualTrainingRequest individualTrainingsRequestModel
    ) throws PastDateException, StartDateAfterEndDateException {
        String starDateTimeStr = individualTrainingsRequestModel.getStartDateTime();
        String endDateTimeStr = individualTrainingsRequestModel.getEndDateTime();

        LocalDateTime startDateTime = parseDateTime(starDateTimeStr);
        if (LocalDateTime.now(clock).isAfter(startDateTime)) throw new PastDateException();

        LocalDateTime endDateTime = parseDateTime(endDateTimeStr);
        if (startDateTime.isAfter(endDateTime)) throw new StartDateAfterEndDateException();

        return new LocalDateTime[]{startDateTime, endDateTime};
    }

    private void validateIfTrainerIsOccupied(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            UserDocument trainer
    ) throws TrainerOccupiedException {
        CollisionValidator validator = collisionValidator.getCollisionValidator(startDateTime, endDateTime);
        boolean isLocationOccupied = validator.isTrainerOccupied(List.of(trainer));
        if (isLocationOccupied) throw new TrainerOccupiedException();
    }

    @Override
    public IndividualTrainingDTO cancelIndividualTrainingRequest(String trainingId, String clientId)
            throws
            NotExistingIndividualTrainingException,
            UserNotFoundException,
            PastDateException,
            UserIsNotParticipantException,
            IndividualTrainingHasBeenRejectedException,
            AlreadyCancelledIndividualTrainingException {

        IndividualTrainingDocument individualTraining = getAndValidateIndividualTraining(trainingId);
        UserDocument user = getAndValidateUser(clientId);

        validateIfIndividualTrainingIsAboutToTakePlace(individualTraining);
        validateIfUserIsParticipant(individualTraining, user);
        validateIfTrainingHasBeenAlreadyRejected(individualTraining);
        validateIfTrainingHasBeenAlreadyCancelled(individualTraining);

        IndividualTrainingDocument trainingDocumentUpdated = cancelIndividualTraining(individualTraining);

        return mapIndividualTrainingDocumentToDTO(trainingDocumentUpdated);
    }

    @Override
    public BasicTrainingDTO getMyNextTraining(String clientId) throws UserNotFoundException {

        UserDocument user = userDAO.findByUserId(clientId);
        if (user == null) throw new UserNotFoundException();

        List<IndividualTrainingDocument> individualTrainingDocuments = userIndividualTrainingDAO
                .findAllIndividualTrainingsWithStartDateAfterNow(user);

        Optional<IndividualTrainingDocument> nextUserIndividualTrainingDocumentOptional = individualTrainingDocuments
                .stream()
                .sorted(Comparator.nullsLast((d1, d2) -> d1.getStartDateTime().compareTo(d2.getStartDateTime())))
                .findFirst();

        if(nextUserIndividualTrainingDocumentOptional.isEmpty()) return null;

        IndividualTrainingDocument nextUserIndividualTrainingDocument = nextUserIndividualTrainingDocumentOptional.get();
        return mapGroupTrainingToBasicTrainingDTO(nextUserIndividualTrainingDocument);
    }

    private IndividualTrainingDocument getAndValidateIndividualTraining(String trainingId)
            throws NotExistingIndividualTrainingException {
        Optional<IndividualTrainingDocument> trainingDocument = individualTrainingRepository
                .findByIndividualTrainingId(trainingId);
        return trainingDocument.orElseThrow(NotExistingIndividualTrainingException::new);
    }

    private void validateIfIndividualTrainingIsAboutToTakePlace(IndividualTrainingDocument individualTraining)
            throws PastDateException {
        LocalDateTime startDateTime = individualTraining.getStartDateTime();
        if (LocalDateTime.now(clock).isAfter(startDateTime)) throw new PastDateException();
    }

    private void validateIfUserIsParticipant(
            IndividualTrainingDocument trainingDocument,
            UserDocument userDocument
    ) throws UserIsNotParticipantException {
        List<UserDocument> basicList = trainingDocument.getBasicList();
        if (!basicList.contains(userDocument)) throw new UserIsNotParticipantException();
    }

    private void validateIfTrainingHasBeenAlreadyRejected(IndividualTrainingDocument individualTraining)
            throws IndividualTrainingHasBeenRejectedException {
        boolean isRejected = individualTraining.isRejected();
        if (isRejected) throw new IndividualTrainingHasBeenRejectedException();
    }

    private void validateIfTrainingHasBeenAlreadyCancelled(IndividualTrainingDocument individualTraining)
            throws AlreadyCancelledIndividualTrainingException {
        boolean isCancelled = individualTraining.isCancelled();
        if (isCancelled) throw new AlreadyCancelledIndividualTrainingException();
    }

    private IndividualTrainingDocument cancelIndividualTraining(IndividualTrainingDocument training) {
        training.setCancelled(true);
        return individualTrainingRepository.save(training);
    }
}
