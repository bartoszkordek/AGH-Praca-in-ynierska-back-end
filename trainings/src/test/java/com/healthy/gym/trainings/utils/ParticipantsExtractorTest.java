package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.*;
import static org.assertj.core.api.Assertions.assertThat;

class ParticipantsExtractorTest {

    private UserDocument testUser1;
    private UserDocument testUser2;
    private UserDocument testUser3;
    private String clientId1;
    private String clientId2;
    private String clientId3;
    private GroupTrainingDocument groupTrainings;

    @BeforeEach
    void setUp() {
        clientId1 = UUID.randomUUID().toString();
        testUser1 = new UserDocument(
                "TestName1",
                "TestSurname1",
                null,
                null,
                null,
                clientId1
        );
        clientId2 = UUID.randomUUID().toString();
        testUser2 = new UserDocument(
                "TestName2",
                "TestSurname2",
                null,
                null,
                null,
                clientId2
        );
        clientId3 = UUID.randomUUID().toString();
        testUser3 = new UserDocument(
                "TestName3",
                "TestSurname3",
                null,
                null,
                null,
                clientId3
        );
        groupTrainings = new GroupTrainingDocument();
    }

    @Nested
    class WhenGetBasicList {
        @Test
        void shouldReturnProperBasicList() {
            groupTrainings.setBasicList(List.of(testUser1, testUser2, testUser3));
            assertThat(getBasicList(groupTrainings))
                    .hasSize(3)
                    .isEqualTo(
                            List.of(
                                    new BasicUserInfoDTO(clientId1, "TestName1", "TestSurname1",null),
                                    new BasicUserInfoDTO(clientId2, "TestName2", "TestSurname2",null),
                                    new BasicUserInfoDTO(clientId3, "TestName3", "TestSurname3", null)
                            )
                    );
        }

        @Test
        void shouldReturnEmptyList() {
            groupTrainings.setBasicList(List.of());
            assertThat(getBasicList(groupTrainings)).isEmpty();
        }
    }

    @Nested
    class WhenGetReserveList {
        @Test
        void shouldReturnProperReserveList() {
            groupTrainings.setReserveList(List.of(testUser1, testUser2, testUser3));
            assertThat(getReserveList(groupTrainings))
                    .hasSize(3)
                    .isEqualTo(
                            List.of(
                                    new BasicUserInfoDTO(clientId1, "TestName1", "TestSurname1", null),
                                    new BasicUserInfoDTO(clientId2, "TestName2", "TestSurname2", null),
                                    new BasicUserInfoDTO(clientId3, "TestName3", "TestSurname3", null)
                            )
                    );
        }

        @Test
        void shouldReturnEmptyList() {
            groupTrainings.setReserveList(List.of());
            assertThat(getReserveList(groupTrainings)).isEmpty();
        }
    }

    @Nested
    class WhenIsClientAlreadyExistInReserveList {

        @Test
        void shouldReturnTrueWhenUserExists() {
            groupTrainings.setReserveList(List.of(testUser1, testUser2, testUser3));
            assertThat(isClientAlreadyExistInReserveList(groupTrainings, clientId1)).isTrue();
        }

        @Test
        void shouldReturnFalseWhenUserDoesNotExist() {
            groupTrainings.setReserveList(List.of(testUser2, testUser3));
            assertThat(isClientAlreadyExistInReserveList(groupTrainings, clientId1)).isFalse();
        }

        @Test
        void shouldReturnFalseWhenUserIsNull() {
            groupTrainings.setReserveList(List.of(testUser2, testUser3));
            assertThat(isClientAlreadyExistInReserveList(groupTrainings, null)).isFalse();
        }
    }

    @Nested
    class WhenIsClientAlreadyEnrolledToGroupTraining {
        @Test
        void shouldReturnTrueWhenUserExists() {
            groupTrainings.setBasicList(List.of(testUser1, testUser2, testUser3));
            assertThat(isClientAlreadyEnrolledToGroupTraining(groupTrainings, clientId1)).isTrue();
        }

        @Test
        void shouldReturnFalseWhenUserDoesNotExist() {
            groupTrainings.setBasicList(List.of(testUser2, testUser3));
            assertThat(isClientAlreadyEnrolledToGroupTraining(groupTrainings, clientId1)).isFalse();
        }

        @Test
        void shouldReturnFalseWhenUserIsNull() {
            groupTrainings.setBasicList(List.of(testUser2, testUser3));
            assertThat(isClientAlreadyEnrolledToGroupTraining(groupTrainings, null)).isFalse();
        }
    }
}