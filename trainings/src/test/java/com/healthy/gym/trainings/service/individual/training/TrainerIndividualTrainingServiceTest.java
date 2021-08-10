package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestTrainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainerIndividualTrainingServiceTest {

    private IndividualTrainingRepository repository;
    private UserDAO userDAO;
    private LocationDAO locationDAO;
    private TrainerIndividualTrainingService service;
    private String userId;
    private String trainingId;
    private String locationId;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));
        repository = mock(IndividualTrainingRepository.class);
        userDAO = mock(UserDAO.class);
        locationDAO = mock(LocationDAO.class);
        service = new TrainerIndividualTrainingServiceImpl(userDAO, repository, locationDAO, clock);
        userId = UUID.randomUUID().toString();
        trainingId = UUID.randomUUID().toString();
        locationId = UUID.randomUUID().toString();
    }

    @Nested
    class AcceptIndividualTraining {

        private IndividualTrainingDocument training;

        @BeforeEach
        void setUp() {
            training = getTestIndividualTrainingDocument();
        }

        private IndividualTrainingDocument getTestIndividualTrainingDocument() {
            var training = new IndividualTrainingDocument();
            training.setStartDateTime(LocalDateTime.parse("2021-07-10T21:00"));
            training.setTrainers(List.of(getTestTrainer()));
            return training;
        }

        @Test
        void shouldThrowNotExistingIndividualTrainingException() {
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(NotExistingIndividualTrainingException.class);
        }

        @Test
        void shouldThrowPastDateException() {
            training.setStartDateTime(LocalDateTime.parse("2021-07-10T17:00"));
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(null);
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotTrainer() {
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowAccessDeniedException() {
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void shouldThrowAlreadyAcceptedIndividualTrainingException() {
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
            training.setTrainers(List.of(trainer));
            training.setAccepted(true);

            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);

            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(AlreadyAcceptedIndividualTrainingException.class);
        }

        @Test
        void shouldThrowLocationNotFoundException() {
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));

            training.setTrainers(List.of(trainer));

            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            when(locationDAO.findByLocationId(locationId)).thenReturn(null);

            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(LocationNotFoundException.class);
        }

        @Disabled
        @Test
        void shouldThrowLocationOccupiedException() {
            //todo locationOccupiedException
            assertThat(true).isTrue();
        }

        @Test
        void shouldAcceptIndividualTrainingRequest()
                throws UserNotFoundException,
                NotExistingIndividualTrainingException,
                AlreadyAcceptedIndividualTrainingException,
                LocationOccupiedException,
                PastDateException,
                LocationNotFoundException {

            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));

            training.setTrainers(List.of(trainer));

            LocationDocument location = new LocationDocument(
                    UUID.randomUUID().toString(),
                    "TestLocation"
            );

            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            when(locationDAO.findByLocationId(locationId)).thenReturn(location);
            when(repository.save(training)).thenReturn(getSavedTraining(training, location));

            var returnedDTO = service.acceptIndividualTraining(userId, trainingId, locationId);

            assertThat(returnedDTO.isAccepted()).isTrue();
            assertThat(returnedDTO.isCancelled()).isFalse();
            assertThat(returnedDTO.isRejected()).isFalse();
            assertThat(returnedDTO.getLocation()).isEqualTo("TestLocation");
        }

        private IndividualTrainingDocument getSavedTraining(
                IndividualTrainingDocument training,
                LocationDocument locationDocument
        ) {
            var acceptedTraining = new IndividualTrainingDocument(
                    training.getIndividualTrainingId(),
                    training.getTraining(),
                    training.getBasicList(),
                    training.getTrainers(),
                    training.getStartDateTime(),
                    training.getEndDateTime(),
                    locationDocument,
                    training.getRemarks()
            );
            acceptedTraining.setAccepted(true);

            return acceptedTraining;
        }
    }

}