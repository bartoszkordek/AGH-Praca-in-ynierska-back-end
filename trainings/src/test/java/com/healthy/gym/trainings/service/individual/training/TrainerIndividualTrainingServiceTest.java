package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.component.CollisionValidatorComponent;
import com.healthy.gym.trainings.component.CollisionValidatorComponentImpl;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.group.training.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.AlreadyRejectedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.test.utils.TestDocumentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestTrainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainerIndividualTrainingServiceTest {

    private IndividualTrainingRepository individualTrainingRepository;
    private GroupTrainingsDAO groupTrainingsDAO;
    private UserDAO userDAO;
    private LocationDAO locationDAO;
    private TrainerIndividualTrainingService service;
    private String userId;
    private String trainingId;
    private String locationId;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));
        individualTrainingRepository = mock(IndividualTrainingRepository.class);
        userDAO = mock(UserDAO.class);
        locationDAO = mock(LocationDAO.class);
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        CollisionValidatorComponent collisionValidatorComponent =
                new CollisionValidatorComponentImpl(groupTrainingsDAO, individualTrainingRepository);
        service = new TrainerIndividualTrainingServiceImpl(
                userDAO,
                collisionValidatorComponent,
                individualTrainingRepository,
                locationDAO,
                clock
        );
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
            training.setEndDateTime(LocalDateTime.parse("2021-07-10T22:00"));
            training.setTrainers(List.of(getTestTrainer()));
            return training;
        }

        @Test
        void shouldThrowNotExistingIndividualTrainingException() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(NotExistingIndividualTrainingException.class);
        }

        @Test
        void shouldThrowPastDateException() {
            training.setStartDateTime(LocalDateTime.parse("2021-07-10T17:00"));
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(null);
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotTrainer() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            assertThatThrownBy(() -> service.acceptIndividualTraining(userId, trainingId, locationId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowAccessDeniedException() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
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

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
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

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            when(locationDAO.findByLocationId(locationId)).thenReturn(null);

            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(LocationNotFoundException.class);
        }

        @Test
        void shouldThrowLocationOccupiedExceptionByGroupTrainings() {
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));

            training.setTrainers(List.of(trainer));

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);

            LocationDocument location = getTestLocationDocument();
            when(locationDAO.findByLocationId(locationId)).thenReturn(location);

            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);
            when(groupTrainingsDAO
                    .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
            ).thenReturn(List.of(
                    TestDocumentUtil.getTestGroupTraining(
                            "2021-07-10T19:00", "2021-07-10T20:30", location
                    ),
                    TestDocumentUtil.getTestGroupTraining(
                            "2021-07-10T21:00", "2021-07-10T22:00", location
                    )
            ));

            when(individualTrainingRepository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
                    startDateTime, endDateTime, Sort.by("startDateTime")
            )).thenReturn(List.of());

            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(LocationOccupiedException.class);
        }

        @Test
        void shouldThrowLocationOccupiedExceptionByIndividualTrainings() {
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));

            training.setTrainers(List.of(trainer));

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);

            LocationDocument location = getTestLocationDocument();
            when(locationDAO.findByLocationId(locationId)).thenReturn(location);

            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);

            when(groupTrainingsDAO
                    .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
            ).thenReturn(List.of());

            when(individualTrainingRepository
                    .findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndCancelledIsFalseAndRejectedIsFalse(
                            startDateTime, endDateTime, Sort.by("startDateTime"))
            ).thenReturn(List.of(
                    TestDocumentUtil.getTestIndividualTraining(
                            "2021-07-10T19:00", "2021-07-10T20:30", location
                    ),
                    TestDocumentUtil.getTestIndividualTraining(
                            "2021-07-10T21:00", "2021-07-10T21:50", location
                    )
            ));

            assertThatThrownBy(
                    () -> service.acceptIndividualTraining(userId, trainingId, locationId)
            ).isInstanceOf(LocationOccupiedException.class);
        }

        private LocationDocument getTestLocationDocument() {
            return new LocationDocument(UUID.randomUUID().toString(), "TestLocation");
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

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            when(locationDAO.findByLocationId(locationId)).thenReturn(location);

            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse("2021-07-10"), LocalTime.MAX);
            when(groupTrainingsDAO
                    .findAllByStartDateIsAfterAndEndDateIsBefore(startDateTime, endDateTime, Sort.by("startDate"))
            ).thenReturn(List.of());

            when(individualTrainingRepository
                    .findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(startDateTime, endDateTime, Sort.by("startDateTime"))
            ).thenReturn(List.of());

            when(individualTrainingRepository.save(training)).thenReturn(getSavedTraining(training, location));


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

    @Nested
    class RejectIndividualTraining {

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
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.rejectIndividualTraining(userId, trainingId))
                    .isInstanceOf(NotExistingIndividualTrainingException.class);
        }

        @Test
        void shouldThrowPastDateException() {
            training.setStartDateTime(LocalDateTime.parse("2021-07-10T17:00"));
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            assertThatThrownBy(() -> service.rejectIndividualTraining(userId, trainingId))
                    .isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(null);
            assertThatThrownBy(() -> service.rejectIndividualTraining(userId, trainingId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserIsNotTrainer() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            assertThatThrownBy(() -> service.rejectIndividualTraining(userId, trainingId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowAccessDeniedException() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            assertThatThrownBy(
                    () -> service.rejectIndividualTraining(userId, trainingId)
            ).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void shouldThrowAlreadyRejectedIndividualTrainingException() {
            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
            training.setTrainers(List.of(trainer));
            training.setRejected(true);

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);

            assertThatThrownBy(
                    () -> service.rejectIndividualTraining(userId, trainingId)
            ).isInstanceOf(AlreadyRejectedIndividualTrainingException.class);
        }

        @Test
        void shouldRejectIndividualTrainingRequest()
                throws UserNotFoundException,
                NotExistingIndividualTrainingException,
                PastDateException,
                AlreadyRejectedIndividualTrainingException {

            var trainer = new UserDocument();
            trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));

            training.setTrainers(List.of(trainer));

            LocationDocument location = new LocationDocument(
                    UUID.randomUUID().toString(),
                    "TestLocation"
            );

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.of(training));
            when(userDAO.findByUserId(userId)).thenReturn(trainer);
            when(individualTrainingRepository.save(training)).thenReturn(getSavedTraining(training, location));

            var returnedDTO = service.rejectIndividualTraining(userId, trainingId);

            assertThat(returnedDTO.isAccepted()).isFalse();
            assertThat(returnedDTO.isCancelled()).isFalse();
            assertThat(returnedDTO.isRejected()).isTrue();
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
            acceptedTraining.setRejected(true);

            return acceptedTraining;
        }
    }

}