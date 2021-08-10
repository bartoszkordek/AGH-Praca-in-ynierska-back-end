package com.healthy.gym.trainings.service.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.individual.training.IndividualTrainingRepository;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmployeeIndividualTrainingServiceTest {

    private IndividualTrainingRepository repository;
    private EmployeeIndividualTrainingService service;

    @BeforeEach
    void setUp() {
        repository = mock(IndividualTrainingRepository.class);
        service = new EmployeeIndividualTrainingServiceImpl(repository);
    }

    @Nested
    class GetIndividualTrainingById {

        private String trainingId;
        private TrainingTypeDocument trainingType;
        private UserDocument user;
        private UserDocument trainer;
        private LocationDocument location;
        private IndividualTrainingDocument trainingDocument;

        @BeforeEach
        void setUp() {
            trainingId = UUID.randomUUID().toString();

            trainingType = getTestTrainingType();
            user = getTestUser();
            trainer = getTestTrainer();
            location = getTestLocation();

            trainingDocument = new IndividualTrainingDocument(
                    trainingId,
                    trainingType,
                    List.of(user),
                    List.of(trainer),
                    LocalDateTime.parse("2020-10-10T10:00"),
                    LocalDateTime.parse("2020-10-10T12:00"),
                    location,
                    "Test remarks"
            );
        }

        @Test
        void shouldThrowNotExistingIndividualTrainingExceptionWhenNoTrainingFound() {
            when(repository.findByIndividualTrainingId(trainingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getIndividualTrainingById(trainingId))
                    .isInstanceOf(NotExistingIndividualTrainingException.class);
        }

        @Nested
        class ShouldReturnIndividualTraining {
            private IndividualTrainingDTO training;

            @BeforeEach
            void setUp() throws NotExistingIndividualTrainingException {
                when(repository.findByIndividualTrainingId(trainingId))
                        .thenReturn(Optional.of(trainingDocument));

                training = service.getIndividualTrainingById(trainingId);
            }

            @Test
            void shouldReturnProperTrainingId() {
                assertThat(training.getIndividualTrainingId()).isEqualTo(trainingId);
            }

            @Test
            void shouldReturnProperLocation() {
                assertThat(training.getLocation()).isEqualTo(location.getName());
            }

            @Test
            void shouldReturnProperTrainerLists() {
                BasicUserInfoDTO trainerDTO = new BasicUserInfoDTO(
                        trainer.getUserId(),
                        trainer.getName(),
                        trainer.getSurname(),
                        null
                );
                assertThat(training.getTrainers()).isEqualTo(List.of(trainerDTO));
            }

            @Test
            void shouldReturnProperParticipantList() {
                BasicUserInfoDTO participant = new BasicUserInfoDTO(
                        user.getUserId(),
                        user.getName(),
                        user.getSurname(),
                        null
                );
                assertThat(training.getParticipants().getBasicList()).isEqualTo(List.of(participant));
            }

            @Test
            void shouldReturnProperStartDate() {
                assertThat(training.getStartDate()).isEqualTo("2020-10-10T10:00");
            }

            @Test
            void shouldReturnProperEndDate() {
                assertThat(training.getEndDate()).isEqualTo("2020-10-10T12:00");
            }

            @Test
            void shouldReturnProperTrainingType() {
                assertThat(training.getTitle()).isEqualTo(trainingType.getName());
            }
        }
    }

    @Nested
    class GetAllAcceptedIndividualTrainings {

        @Test
        void shouldThrowStartDateAfterEndDateExceptionWhenInvalidDataProvided() {
            assertThatThrownBy(
                    () -> service.getAllAcceptedIndividualTrainings(
                            "2020-10-12",
                            "2020-10-10",
                            0,
                            5
                    )
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldThrowNoIndividualTrainingFoundExceptionWhenNoTrainingFound() {
            when(repository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndAcceptedIsTrue(
                    LocalDateTime.of(LocalDate.parse("2020-10-10"), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse("2020-10-12"), LocalTime.MAX),
                    PageRequest.of(0, 5, Sort.by("startDateTime"))
            )).thenReturn(new PageImpl<>(List.of()));

            assertThatThrownBy(
                    () -> service.getAllAcceptedIndividualTrainings(
                            "2020-10-10",
                            "2020-10-12",
                            0,
                            5
                    )
            ).isInstanceOf(NoIndividualTrainingFoundException.class);
        }

        @Test
        void shouldReturnAllAcceptedIndividualTrainings()
                throws NoIndividualTrainingFoundException, StartDateAfterEndDateException {
            var trainings = List.of(
                    getTestIndividualTraining("2020-10-10T10:00", "2020-10-10T12:00"),
                    getTestIndividualTraining("2020-10-10T12:00", "2020-10-10T13:00"),
                    getTestIndividualTraining("2020-10-11T12:00", "2020-10-11T13:00"),
                    getTestIndividualTraining("2020-10-12T12:00", "2020-10-12T13:00"),
                    getTestIndividualTraining("2020-10-12T13:00", "2020-10-12T14:00")
            );
            when(repository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBeforeAndAcceptedIsTrue(
                    LocalDateTime.of(LocalDate.parse("2020-10-10"), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse("2020-10-12"), LocalTime.MAX),
                    PageRequest.of(0, 5, Sort.by("startDateTime"))
            )).thenReturn(new PageImpl<>(trainings));

            List<IndividualTrainingDTO> list = service.getAllAcceptedIndividualTrainings(
                    "2020-10-10",
                    "2020-10-12",
                    0,
                    5
            );

            assertThat(list.size()).isEqualTo(5);
            assertThat(list.get(0).getStartDate()).isEqualTo("2020-10-10T10:00");
            assertThat(list.get(0).getEndDate()).isEqualTo("2020-10-10T12:00");

            assertThat(list.get(1).getStartDate()).isEqualTo("2020-10-10T12:00");
            assertThat(list.get(1).getEndDate()).isEqualTo("2020-10-10T13:00");
        }
    }

    @Nested
    class GetIndividualTrainings {

        @Test
        void shouldThrowStartDateAfterEndDateExceptionWhenInvalidDataProvided() {
            assertThatThrownBy(
                    () -> service.getIndividualTrainings(
                            "2020-10-12",
                            "2020-10-10",
                            0,
                            5
                    )
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldThrowNoIndividualTrainingFoundExceptionWhenNoTrainingFound() {
            when(repository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
                    LocalDateTime.of(LocalDate.parse("2020-10-10"), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse("2020-10-12"), LocalTime.MAX),
                    PageRequest.of(0, 5, Sort.by("startDateTime"))
            )).thenReturn(new PageImpl<>(List.of()));

            assertThatThrownBy(
                    () -> service.getIndividualTrainings(
                            "2020-10-10",
                            "2020-10-12",
                            0,
                            5
                    )
            ).isInstanceOf(NoIndividualTrainingFoundException.class);
        }

        @Test
        void shouldReturnAllAcceptedIndividualTrainings()
                throws NoIndividualTrainingFoundException, StartDateAfterEndDateException {
            var trainings = List.of(
                    getTestIndividualTraining("2020-10-10T10:00", "2020-10-10T12:00"),
                    getTestIndividualTraining("2020-10-10T12:00", "2020-10-10T13:00"),
                    getTestIndividualTraining("2020-10-11T12:00", "2020-10-11T13:00"),
                    getTestIndividualTraining("2020-10-12T12:00", "2020-10-12T13:00"),
                    getTestIndividualTraining("2020-10-12T13:00", "2020-10-12T14:00")
            );
            when(repository.findAllByStartDateTimeIsAfterAndEndDateTimeIsBefore(
                    LocalDateTime.of(LocalDate.parse("2020-10-10"), LocalTime.MIN),
                    LocalDateTime.of(LocalDate.parse("2020-10-12"), LocalTime.MAX),
                    PageRequest.of(0, 5, Sort.by("startDateTime"))
            )).thenReturn(new PageImpl<>(trainings));

            List<IndividualTrainingDTO> list = service.getIndividualTrainings(
                    "2020-10-10",
                    "2020-10-12",
                    0,
                    5
            );

            assertThat(list.size()).isEqualTo(5);
            assertThat(list.get(0).getStartDate()).isEqualTo("2020-10-10T10:00");
            assertThat(list.get(0).getEndDate()).isEqualTo("2020-10-10T12:00");

            assertThat(list.get(1).getStartDate()).isEqualTo("2020-10-10T12:00");
            assertThat(list.get(1).getEndDate()).isEqualTo("2020-10-10T13:00");
        }
    }

}