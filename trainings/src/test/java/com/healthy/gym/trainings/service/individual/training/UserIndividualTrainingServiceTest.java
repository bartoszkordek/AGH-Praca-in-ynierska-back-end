package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.component.CollisionValidatorComponent;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.data.repository.individual.training.UserIndividualTrainingDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidTrainerSpecifiedException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import com.healthy.gym.trainings.service.NotificationService;
import com.healthy.gym.trainings.test.utils.TestDocumentUtil;
import com.healthy.gym.trainings.utils.CollisionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserIndividualTrainingServiceTest {

    private CollisionValidatorComponent collisionValidator;
    private IndividualTrainingRepository individualTrainingRepository;
    private UserIndividualTrainingDAO userIndividualTrainingDAO;
    private UserDAO userDAO;
    private TrainingTypeDAO trainingTypeDAO;
    private UserIndividualTrainingService service;
    private String userId;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T17:00:00.00Z"), ZoneId.of("Europe/Warsaw"));
        collisionValidator = mock(CollisionValidatorComponent.class);
        individualTrainingRepository = mock(IndividualTrainingRepository.class);
        userIndividualTrainingDAO = mock(UserIndividualTrainingDAO.class);
        userDAO = mock(UserDAO.class);
        trainingTypeDAO = mock(TrainingTypeDAO.class);
        NotificationService notificationService = mock(NotificationService.class);
        doNothing().when(notificationService).sendNotificationWhenCreateIndividualTrainingRequest(any(), any(), any());
        service = new UserIndividualTrainingServiceImpl(
                collisionValidator,
                individualTrainingRepository,
                userIndividualTrainingDAO,
                userDAO,
                trainingTypeDAO,
                notificationService,
                clock
        );
        userId = UUID.randomUUID().toString();
    }

    @Nested
    class GetMyAllTrainings {
        private String startDate;
        private String endDate;

        @BeforeEach
        void setUp() {
            startDate = "2020-10-10";
            endDate = "2020-10-11";
        }

        @Test
        void shouldStartDateAfterEndDateException() {
            assertThatThrownBy(
                    () -> service.getMyAllTrainings(userId, startDate, "2020-10-09")
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldUserNotFoundException() {
            when(userDAO.findByUserId(anyString())).thenReturn(null);
            assertThatThrownBy(
                    () -> service.getMyAllTrainings(userId, startDate, endDate)
            ).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldNoIndividualTrainingFoundException() {
            var user = new UserDocument();
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
            when(userIndividualTrainingDAO
                    .findAllIndividualTrainingsWithDatesByUserDocument(user, startDateTime, endDateTime)
            ).thenReturn(List.of());

            assertThatThrownBy(
                    () -> service.getMyAllTrainings(userId, startDate, endDate)
            ).isInstanceOf(NoIndividualTrainingFoundException.class);
        }

        @Test
        void shouldReturnAllUserIndividualTrainings()
                throws UserNotFoundException, NoIndividualTrainingFoundException, StartDateAfterEndDateException {

            var user = new UserDocument();
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(startDate), LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(endDate), LocalTime.MAX);
            when(userIndividualTrainingDAO
                    .findAllIndividualTrainingsWithDatesByUserDocument(user, startDateTime, endDateTime)
            ).thenReturn(List.of(getTestIndividualTrainingDocument(), getTestIndividualTrainingDocument()));

            var allTrainings = service.getMyAllTrainings(userId, startDate, endDate);
            assertThat(allTrainings.size()).isEqualTo(2);
        }

        private IndividualTrainingDocument getTestIndividualTrainingDocument() {
            var training = new IndividualTrainingDocument();
            training.setStartDateTime(LocalDateTime.parse("2021-07-10T21:00"));
            training.setTrainers(List.of(getTestTrainer()));

            List<UserDocument> basicList = new ArrayList<>(getTestListOfUserDocuments(4));
            basicList.add(getTestTrainer(userId));

            training.setBasicList(basicList);
            return training;
        }

    }

    @Nested
    class CreateIndividualTrainingRequest {
        private IndividualTrainingRequest individualTrainingsRequestModel;
        private String trainerId;

        @BeforeEach
        void setUp() {
            trainerId = UUID.randomUUID().toString();
            individualTrainingsRequestModel = new IndividualTrainingRequest();
            individualTrainingsRequestModel.setStartDateTime("2021-07-10T19:00");
            individualTrainingsRequestModel.setEndDateTime("2021-07-10T20:00");
            individualTrainingsRequestModel.setTrainerId(trainerId);
            individualTrainingsRequestModel.setRemarks("Test remarks");
        }

        @Test
        void shouldThrowUserNotFoundException() {
            when(userDAO.findByUserId(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowTrainerNotFoundException() {
            when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
            when(userDAO.findByUserId(trainerId)).thenReturn(null);

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(TrainerNotFoundException.class);
        }

        @Test
        void shouldThrowInvalidTrainerSpecifiedException() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            when(userDAO.findByUserId(trainerId)).thenReturn(getTestUser());

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(InvalidTrainerSpecifiedException.class);
        }

        @Test
        void shouldThrowStartDateAfterEndDateException() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            when(userDAO.findByUserId(trainerId)).thenReturn(getTestTrainer());
            individualTrainingsRequestModel.setEndDateTime("2021-07-10T16:00");

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldThrowPastDateException() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            when(userDAO.findByUserId(trainerId)).thenReturn(getTestTrainer());
            individualTrainingsRequestModel.setStartDateTime("2021-07-10T17:00");
            individualTrainingsRequestModel.setEndDateTime("2021-07-10T18:00");

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldThrowTrainerOccupiedExceptionByGroupTrainings() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            var trainer = getTestTrainer();
            when(userDAO.findByUserId(trainerId)).thenReturn(trainer);
            LocalDateTime startDateTime = LocalDateTime.parse("2021-07-10T19:00");
            LocalDateTime endDateTime = LocalDateTime.parse("2021-07-10T20:00");
            when(collisionValidator.getCollisionValidator(startDateTime, endDateTime))
                    .thenReturn(new CollisionValidator(
                            getTestGroupTrainingDocumentList(trainer),
                            List.of(),
                            startDateTime,
                            endDateTime
                    ));

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(TrainerOccupiedException.class);
        }

        private List<GroupTrainingDocument> getTestGroupTrainingDocumentList(UserDocument trainer) {
            return List.of(
                    TestDocumentUtil.getTestGroupTraining(
                            "2021-07-10T19:00", "2021-07-10T20:30", List.of(trainer)
                    ),
                    TestDocumentUtil.getTestGroupTraining(
                            "2021-07-10T21:00", "2021-07-10T22:00", List.of(trainer)
                    )
            );
        }

        @Test
        void shouldThrowTrainerOccupiedExceptionByIndividualTrainings() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            var trainer = getTestTrainer();
            when(userDAO.findByUserId(trainerId)).thenReturn(trainer);

            LocalDateTime startDateTime = LocalDateTime.parse("2021-07-10T19:00");
            LocalDateTime endDateTime = LocalDateTime.parse("2021-07-10T20:00");
            when(collisionValidator.getCollisionValidator(startDateTime, endDateTime))
                    .thenReturn(new CollisionValidator(
                            List.of(),
                            getTestIndividualTrainingDocumentList(trainer),
                            startDateTime,
                            endDateTime
                    ));

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(TrainerOccupiedException.class);
        }

        private List<IndividualTrainingDocument> getTestIndividualTrainingDocumentList(UserDocument trainer) {
            return List.of(
                    TestDocumentUtil.getTestIndividualTraining(
                            "2021-07-10T19:00", "2021-07-10T20:30", List.of(trainer)
                    ),
                    TestDocumentUtil.getTestIndividualTraining(
                            "2021-07-10T21:00", "2021-07-10T22:00", List.of(trainer)
                    ));
        }

        @Test
        void shouldThrowTrainingTypeNotFoundException() {
            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            when(userDAO.findByUserId(trainerId)).thenReturn(getTestTrainer());

            LocalDateTime startDateTime = LocalDateTime.parse("2021-07-10T19:00");
            LocalDateTime endDateTime = LocalDateTime.parse("2021-07-10T20:00");
            when(collisionValidator.getCollisionValidator(startDateTime, endDateTime))
                    .thenReturn(new CollisionValidator(
                            List.of(),
                            List.of(),
                            startDateTime,
                            endDateTime
                    ));

            when(trainingTypeDAO.findByName(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> service.createIndividualTrainingRequest(individualTrainingsRequestModel, userId)
            ).isInstanceOf(TrainingTypeNotFoundException.class);
        }

        @Test
        void shouldSaveAndReturn() throws UserNotFoundException,
                InvalidTrainerSpecifiedException,
                PastDateException,
                StartDateAfterEndDateException,
                TrainerOccupiedException,
                TrainerNotFoundException, TrainingTypeNotFoundException {

            when(userDAO.findByUserId(userId)).thenReturn(getTestUser());
            when(userDAO.findByUserId(trainerId)).thenReturn(getTestTrainer());

            LocalDateTime startDateTime = LocalDateTime.parse("2021-07-10T19:00");
            LocalDateTime endDateTime = LocalDateTime.parse("2021-07-10T20:00");
            when(collisionValidator.getCollisionValidator(startDateTime, endDateTime))
                    .thenReturn(new CollisionValidator(
                            List.of(),
                            List.of(),
                            startDateTime,
                            endDateTime
                    ));

            when(trainingTypeDAO.findByName(anyString())).thenReturn(new TrainingTypeDocument());

            var training = getTestIndividualTraining("2021-07-10T18:00", "2021-07-10T19:00");
            when(individualTrainingRepository.save(any())).thenReturn(training);

            var returnTraining = service
                    .createIndividualTrainingRequest(individualTrainingsRequestModel, userId);

            assertThat(returnTraining.getStartDate()).isEqualTo("2021-07-10T18:00");
            assertThat(returnTraining.getEndDate()).isEqualTo("2021-07-10T19:00");
            assertThat(returnTraining.getParticipants().getBasicList().size())
                    .isEqualTo(training.getBasicList().size());
        }
    }

    @Nested
    class CancelIndividualTrainingRequest {
        private String trainingId;

        @BeforeEach
        void setUp() {
            trainingId = UUID.randomUUID().toString();
        }

        @Test
        void shouldThrowNotExistingIndividualTrainingException() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(NotExistingIndividualTrainingException.class);
        }

        @Test
        void shouldThrowUserNotFoundException() {
            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(new IndividualTrainingDocument()));
            when(userDAO.findByUserId(anyString())).thenReturn(null);

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void shouldThrowPastDateException() {
            var user = getTestUser();
            var training = getTestIndividualTraining("2021-07-10T17:00", "2021-07-10T19:00");

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(training));
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldThrowUserIsNotParticipantException() {
            var user = getTestUser();
            var training = getTestIndividualTraining("2021-07-10T21:00", "2021-07-10T22:00");

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(training));
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(UserIsNotParticipantException.class);
        }

        @Test
        void shouldThrowIndividualTrainingHasBeenRejectedException() {
            var user = getTestUser();
            var training = getTestIndividualTraining("2021-07-10T21:00", "2021-07-10T22:00");
            training.setRejected(true);
            training.setBasicList(List.of(user));

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(training));
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(IndividualTrainingHasBeenRejectedException.class);
        }

        @Test
        void shouldThrowAlreadyCancelledIndividualTrainingException() {
            var user = getTestUser();
            var training = getTestIndividualTraining("2021-07-10T21:00", "2021-07-10T22:00");
            training.setCancelled(true);
            training.setBasicList(List.of(user));

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(training));
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            assertThatThrownBy(
                    () -> service.cancelIndividualTrainingRequest(trainingId, userId)
            ).isInstanceOf(AlreadyCancelledIndividualTrainingException.class);
        }

        @Test
        void shouldReturnDTO() throws UserNotFoundException,
                UserIsNotParticipantException,
                NotExistingIndividualTrainingException,
                PastDateException,
                IndividualTrainingHasBeenRejectedException,
                AlreadyCancelledIndividualTrainingException {

            var user = getTestUser();
            var training = getTestIndividualTraining("2021-07-10T21:00", "2021-07-10T22:00");
            training.setBasicList(List.of(user));

            when(individualTrainingRepository.findByIndividualTrainingId(trainingId))
                    .thenReturn(Optional.of(training));
            when(userDAO.findByUserId(anyString())).thenReturn(user);

            var trainingCancelled = getTestIndividualTraining("2021-07-10T21:00", "2021-07-10T22:00");
            trainingCancelled.setBasicList(List.of(user));
            trainingCancelled.setCancelled(true);

            when(individualTrainingRepository.save(any())).thenReturn(trainingCancelled);

            var returnDTO = service.cancelIndividualTrainingRequest(trainingId, userId);

            assertThat(returnDTO.isCancelled()).isTrue();
        }
    }
}