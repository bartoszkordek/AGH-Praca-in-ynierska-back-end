package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.*;
import static com.healthy.gym.trainings.utils.DateParser.parseDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class CollisionValidatorTest {

    private CollisionValidator validator;
    private List<GroupTrainingDocument> groupTrainingList;
    private List<IndividualTrainingDocument> individualTrainingList;

    @Nested
    class IsLocationOccupied {
        private LocationDocument location;

        @BeforeEach
        void setUp() {
            location = getTestLocation();
            groupTrainingList = getTestGroupTrainingDocumentListSortedByStartDate();
            individualTrainingList = getTestIndividualTrainingDocumentListSortedByStartDate();
        }

        private List<GroupTrainingDocument> getTestGroupTrainingDocumentListSortedByStartDate() {
            return List.of(
                    getTestGroupTraining("2020-10-10T08:30", "2020-10-10T09:45"),
                    getTestGroupTraining("2020-10-10T10:00", "2020-10-10T11:00", location),
                    getTestGroupTraining("2020-10-10T11:10", "2020-10-10T11:45"),
                    getTestGroupTraining("2020-10-10T12:00", "2020-10-10T13:00", location),
                    getTestGroupTraining("2020-10-10T13:00", "2020-10-10T15:00", location),
                    getTestGroupTraining("2020-10-10T16:00", "2020-10-10T16:45"),

                    getTestGroupTraining("2020-10-11T11:00", "2020-10-11T12:00", location),
                    getTestGroupTraining("2020-10-11T13:00", "2020-10-11T14:45"),
                    getTestGroupTraining("2020-10-11T15:00", "2020-10-11T16:00", location),

                    getTestGroupTraining("2020-10-12T15:00", "2020-10-12T16:45"),
                    getTestGroupTraining("2020-10-12T16:00", "2020-10-12T17:00", location),
                    getTestGroupTraining("2020-10-12T17:00", "2020-10-12T18:00", location),

                    getTestGroupTraining("2020-10-13T10:00", "2020-10-13T12:00", location)
            );
        }

        private List<IndividualTrainingDocument> getTestIndividualTrainingDocumentListSortedByStartDate() {
            return List.of(
                    getTestIndividualTraining("2020-10-10T08:30", "2020-10-10T09:45"),
                    getTestIndividualTraining("2020-10-10T11:00", "2020-10-10T11:30", location),
                    getTestIndividualTraining("2020-10-10T11:30", "2020-10-10T12:15"),
                    getTestIndividualTraining("2020-10-10T16:45", "2020-10-10T17:30", location),
                    getTestIndividualTraining("2020-10-10T17:00", "2020-10-10T17:55"),
                    getTestIndividualTraining("2020-10-10T20:00", "2020-10-10T20:30", location)
            );
        }

        @ParameterizedTest
        @CsvSource({
                "2020-10-10T08:00,2020-10-10T10:50",
                "2020-10-10T11:00,2020-10-10T11:50",
                "2020-10-12T15:00,2020-10-12T16:30",
                "2020-10-10T12:00,2020-10-10T12:50",
                "2020-10-10T12:45,2020-10-10T13:50",
                "2020-10-10T19:45,2020-10-10T20:30",
        })
        void shouldReturnTrueWhenLocationIsOccupied(String startDateTime, String endDateTime) {
            LocalDateTime start = parseDateTime(startDateTime);
            LocalDateTime end = parseDateTime(endDateTime);

            validator = new CollisionValidator(groupTrainingList, individualTrainingList, start, end);
            boolean isCollision = validator.isLocationOccupied(location);

            assertThat(isCollision).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "2020-10-10T08:00,2020-10-10T10:00",
                "2020-10-12T15:00,2020-10-12T16:00",
                "2020-10-10T21:00,2020-10-10T22:00",
                "2020-10-10T17:30,2020-10-10T18:50",
        })
        void shouldReturnFalseWhenLocationIsNotOccupied(String startDateTime, String endDateTime) {
            LocalDateTime start = parseDateTime(startDateTime);
            LocalDateTime end = parseDateTime(endDateTime);

            validator = new CollisionValidator(groupTrainingList, individualTrainingList, start, end);
            boolean isCollision = validator.isLocationOccupied(location);

            assertThat(isCollision).isFalse();
        }
    }

    @Nested
    class IsTrainerOccupied {
        private UserDocument trainer1;
        private UserDocument trainer2;

        @BeforeEach
        void setUp() {
            trainer1 = getTestTrainer();
            trainer2 = getTestTrainer();
            groupTrainingList = getTestGroupTrainingDocumentListSortedByStartDate();
            individualTrainingList = getTestIndividualTrainingDocumentListSortedByStartDate();
        }

        private List<GroupTrainingDocument> getTestGroupTrainingDocumentListSortedByStartDate() {
            return List.of(
                    getTestGroupTraining("2020-10-10T08:30", "2020-10-10T09:45"),
                    getTestGroupTraining(
                            "2020-10-10T10:00", "2020-10-10T11:00", List.of(trainer1)
                    ),
                    getTestGroupTraining("2020-10-10T11:10", "2020-10-10T11:45"),
                    getTestGroupTraining(
                            "2020-10-10T12:00", "2020-10-10T13:00", List.of(trainer1, trainer2)
                    ),
                    getTestGroupTraining(
                            "2020-10-10T13:00", "2020-10-10T15:00", List.of(trainer1)
                    ),
                    getTestGroupTraining("2020-10-10T16:00", "2020-10-10T16:45"),

                    getTestGroupTraining(
                            "2020-10-11T11:00", "2020-10-11T12:00", List.of(trainer1, trainer2)
                    ),
                    getTestGroupTraining("2020-10-11T13:00", "2020-10-11T14:45"),
                    getTestGroupTraining(
                            "2020-10-11T15:00", "2020-10-11T16:00", List.of(trainer1)
                    ),

                    getTestGroupTraining("2020-10-12T15:00", "2020-10-12T16:45"),
                    getTestGroupTraining(
                            "2020-10-12T16:00", "2020-10-12T17:00", List.of(trainer1, trainer2)
                    ),
                    getTestGroupTraining(
                            "2020-10-12T17:00", "2020-10-12T18:00", List.of(trainer2)
                    ),

                    getTestGroupTraining(
                            "2020-10-13T10:00", "2020-10-13T12:00", List.of(trainer1, trainer2)
                    )
            );
        }

        private List<IndividualTrainingDocument> getTestIndividualTrainingDocumentListSortedByStartDate() {
            return List.of(
                    getTestIndividualTraining("2020-10-10T08:30", "2020-10-10T09:45"),
                    getTestIndividualTraining(
                            "2020-10-10T11:00", "2020-10-10T11:30", List.of(trainer1, trainer2)
                    ),
                    getTestIndividualTraining("2020-10-10T11:30", "2020-10-10T12:15"),
                    getTestIndividualTraining(
                            "2020-10-10T16:45", "2020-10-10T17:30", List.of(trainer2)
                    ),
                    getTestIndividualTraining("2020-10-10T17:00", "2020-10-10T17:55"),
                    getTestIndividualTraining(
                            "2020-10-10T20:00", "2020-10-10T20:30", List.of(trainer1)
                    )
            );
        }

        @ParameterizedTest
        @CsvSource({
                "2020-10-10T08:00,2020-10-10T10:50",
                "2020-10-10T11:00,2020-10-10T11:50",
                "2020-10-12T15:00,2020-10-12T16:30",
                "2020-10-10T12:00,2020-10-10T12:50",
                "2020-10-10T12:45,2020-10-10T13:50",
                "2020-10-10T19:45,2020-10-10T20:30",
        })
        void shouldReturnTrueWhenAnyTrainerIsOccupied(String startDateTime, String endDateTime) {
            LocalDateTime start = parseDateTime(startDateTime);
            LocalDateTime end = parseDateTime(endDateTime);

            validator = new CollisionValidator(groupTrainingList, individualTrainingList, start, end);
            boolean isCollision = validator.isTrainerOccupied(List.of(trainer1, trainer2));

            assertThat(isCollision).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "2020-10-10T08:00,2020-10-10T10:50",
                "2020-10-10T11:00,2020-10-10T11:50",
                "2020-10-12T15:00,2020-10-12T16:30",
                "2020-10-10T12:00,2020-10-10T12:50",
                "2020-10-10T12:45,2020-10-10T13:50",
                "2020-10-10T19:45,2020-10-10T20:30",
        })
        void shouldReturnTrueWhenTrainerIsOccupied(String startDateTime, String endDateTime) {
            LocalDateTime start = parseDateTime(startDateTime);
            LocalDateTime end = parseDateTime(endDateTime);

            validator = new CollisionValidator(groupTrainingList, individualTrainingList, start, end);
            boolean isCollision = validator.isTrainerOccupied(List.of(trainer1));

            assertThat(isCollision).isTrue();
        }

        @ParameterizedTest
        @CsvSource({
                "2020-10-10T08:00,2020-10-10T10:00",
                "2020-10-12T15:00,2020-10-12T16:00",
                "2020-10-10T21:00,2020-10-10T22:00",
                "2020-10-10T17:30,2020-10-10T18:50",
        })
        void shouldReturnFalseWhenTrainerIsNotOccupied(String startDateTime, String endDateTime) {
            LocalDateTime start = parseDateTime(startDateTime);
            LocalDateTime end = parseDateTime(endDateTime);

            validator = new CollisionValidator(groupTrainingList, individualTrainingList, start, end);
            boolean isCollision = validator.isTrainerOccupied(List.of(trainer1));

            assertThat(isCollision).isFalse();
        }
    }
}