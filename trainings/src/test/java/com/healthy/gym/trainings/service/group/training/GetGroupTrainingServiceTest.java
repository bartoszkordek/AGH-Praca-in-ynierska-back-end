package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.group.training.UniversalGroupTrainingDAO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.utils.StartEndDateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestGroupTraining;
import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.getTestTrainingType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetGroupTrainingServiceTest {

    private TrainingTypeDAO trainingTypeDAO;
    private UniversalGroupTrainingService universalGroupTrainingService;
    private String trainingTypeId;
    private List<GroupTrainingDocument> groupTrainingDocumentList;
    private TrainingTypeDocument trainingTypeDocument;

    @BeforeEach
    void setUp() throws StartDateAfterEndDateException {
        UniversalGroupTrainingDAO universalGroupTrainingDAO = mock(UniversalGroupTrainingDAO.class);
        trainingTypeDAO = mock(TrainingTypeDAO.class);
        universalGroupTrainingService = new UniversalGroupTrainingServiceImpl(
                universalGroupTrainingDAO,
                trainingTypeDAO
        );
        trainingTypeId = UUID.randomUUID().toString();
        StartEndDateValidator validator = new StartEndDateValidator("2020-10-10", "2020-10-11");

        groupTrainingDocumentList = List.of(
                getTestGroupTraining("2020-10-01T10:00", "2020-10-01T12:00"),
                getTestGroupTraining("2020-10-02T10:00", "2020-10-03T12:00"),
                getTestGroupTraining("2020-10-03T10:00", "2020-10-04T12:00")
        );

        trainingTypeDocument = getTestTrainingType();

        when(universalGroupTrainingDAO
                .getGroupTrainingDocuments(
                        validator.getBeginningOfStartDate(),
                        validator.getEndOfEndDate()
                )
        ).thenReturn(groupTrainingDocumentList);

        when(universalGroupTrainingDAO
                .getGroupTrainingDocuments(
                        validator.getBeginningOfStartDate(),
                        validator.getEndOfEndDate()
                )
        ).thenReturn(groupTrainingDocumentList);
    }


    @Nested
    class WhenGetGroupTrainingsWithParticipants {
        @Test
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsWithParticipants("2020-10-10", "2020-10-01")
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldReturnList() throws StartDateAfterEndDateException {
            var result = universalGroupTrainingService
                    .getGroupTrainingsWithParticipants("2020-10-01", "2020-10-11");

            for (int i = 0; i < result.size(); i++) {
                GroupTrainingDTO training = result.get(i);
                assertThat(training.getLocation())
                        .isEqualTo(groupTrainingDocumentList.get(i).getLocation().getName());
                assertThat(training.getGroupTrainingId())
                        .isEqualTo(groupTrainingDocumentList.get(i).getGroupTrainingId());
                assertThat(training.getParticipants().getBasicList().size())
                        .isEqualTo(groupTrainingDocumentList.get(i).getBasicList().size());
                assertThat(training.getParticipants().getReserveList().size())
                        .isEqualTo(groupTrainingDocumentList.get(i).getReserveList().size());
            }
        }
    }

    @Nested
    class WhenGetGroupTrainingsWithoutParticipants {
        @Test
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsWithoutParticipants("2020-10-10", "2020-10-01")
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldReturnList() throws StartDateAfterEndDateException {
            var result = universalGroupTrainingService
                    .getGroupTrainingsWithoutParticipants("2020-10-01", "2020-10-11");

            for (int i = 0; i < result.size(); i++) {
                GroupTrainingWithoutParticipantsDTO training = result.get(i);
                assertThat(training.getLocation())
                        .isEqualTo(groupTrainingDocumentList.get(i).getLocation().getName());
                assertThat(training.getGroupTrainingId())
                        .isEqualTo(groupTrainingDocumentList.get(i).getGroupTrainingId());
            }
        }
    }

    @Nested
    class WhenGetGroupTrainingsByTypeWithParticipants {
        @Test
        void shouldThrowExceptionWhenTrainingTypeNotFound() {
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsByTypeWithParticipants(
                                    trainingTypeId,
                                    "2020-10-10",
                                    "2020-10-11"
                            )
            ).isInstanceOf(TrainingTypeNotFoundException.class);
        }

        @Test
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(trainingTypeDocument);
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsByTypeWithParticipants(
                                    trainingTypeId,
                                    "2020-10-10",
                                    "2020-10-01"
                            )
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldReturnList() throws StartDateAfterEndDateException, TrainingTypeNotFoundException {
            when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(trainingTypeDocument);
            var result = universalGroupTrainingService
                    .getGroupTrainingsByTypeWithParticipants(trainingTypeId, "2020-10-01", "2020-10-11");

            for (int i = 0; i < result.size(); i++) {
                GroupTrainingDTO training = result.get(i);
                assertThat(training.getLocation())
                        .isEqualTo(groupTrainingDocumentList.get(i).getLocation().getName());
                assertThat(training.getGroupTrainingId())
                        .isEqualTo(groupTrainingDocumentList.get(i).getGroupTrainingId());
                assertThat(training.getParticipants().getBasicList().size())
                        .isEqualTo(groupTrainingDocumentList.get(i).getBasicList().size());
                assertThat(training.getParticipants().getReserveList().size())
                        .isEqualTo(groupTrainingDocumentList.get(i).getReserveList().size());
            }
        }
    }

    @Nested
    class WhenGetGroupTrainingsByTypeWithoutParticipants {
        @Test
        void shouldThrowExceptionWhenTrainingTypeNotFound() {
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsByTypeWithoutParticipants(
                                    trainingTypeId,
                                    "2020-10-10",
                                    "2020-10-11"
                            )
            ).isInstanceOf(TrainingTypeNotFoundException.class);
        }

        @Test
        void shouldThrowExceptionWhenStartDateAfterEndDate() {
            when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(trainingTypeDocument);
            assertThatThrownBy(
                    () -> universalGroupTrainingService
                            .getGroupTrainingsByTypeWithoutParticipants(
                                    trainingTypeId,
                                    "2020-10-10",
                                    "2020-10-01"
                            )
            ).isInstanceOf(StartDateAfterEndDateException.class);
        }

        @Test
        void shouldReturnList() throws StartDateAfterEndDateException, TrainingTypeNotFoundException {
            when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(trainingTypeDocument);
            var result = universalGroupTrainingService
                    .getGroupTrainingsByTypeWithoutParticipants(trainingTypeId, "2020-10-01", "2020-10-11");

            for (int i = 0; i < result.size(); i++) {
                GroupTrainingWithoutParticipantsDTO training = result.get(i);
                assertThat(training.getLocation())
                        .isEqualTo(groupTrainingDocumentList.get(i).getLocation().getName());
                assertThat(training.getGroupTrainingId())
                        .isEqualTo(groupTrainingDocumentList.get(i).getGroupTrainingId());
            }
        }
    }

}
