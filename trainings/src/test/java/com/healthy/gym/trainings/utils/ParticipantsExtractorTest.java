package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
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
                                    new UserResponse(clientId1, "TestName1", "TestSurname1"),
                                    new UserResponse(clientId2, "TestName2", "TestSurname2"),
                                    new UserResponse(clientId3, "TestName3", "TestSurname3")
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
                                    new UserResponse(clientId1, "TestName1", "TestSurname1"),
                                    new UserResponse(clientId2, "TestName2", "TestSurname2"),
                                    new UserResponse(clientId3, "TestName3", "TestSurname3")
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

    @Nested
    class WhenUserIsInBasicList {
        private String userId;
        private GroupTrainingDTO groupTrainingDTO;

        @BeforeEach
        void setUp() {
            userId = UUID.randomUUID().toString();
            groupTrainingDTO = new GroupTrainingDTO();
        }

        @Test
        void shouldReturnTrueWhenUserIsInBasicList() {
            groupTrainingDTO.setBasicList(
                    List.of(
                            getTestUser(userId),
                            getTestUser(),
                            getTestUser(),
                            getTestUser()
                    )
            );

            assertThat(userIsInBasicList(groupTrainingDTO, userId)).isTrue();
        }

        private BasicUserInfoDTO getTestUser() {
            String userId = UUID.randomUUID().toString();
            return getTestUser(userId);
        }

        private BasicUserInfoDTO getTestUser(String userId) {
            var user = new BasicUserInfoDTO();
            user.setUserId(userId);
            return user;
        }

        @Test
        void shouldReturnFalseWhenUserIsNotInBasicList() {
            groupTrainingDTO.setBasicList(
                    List.of(
                            getTestUser(),
                            getTestUser(),
                            getTestUser()
                    )
            );

            assertThat(userIsInBasicList(groupTrainingDTO, userId)).isFalse();
        }
    }

    @Nested
    class WhenRemoveFromList {
        private GroupTrainingDocument groupTraining;

        @BeforeEach
        void setUp() {
            groupTraining = new GroupTrainingDocument();
        }

        private UserDocument getTestUserDocument() {
            return getTestUserDocument(UUID.randomUUID().toString());
        }

        private UserDocument getTestUserDocument(String userId) {
            var user = new UserDocument();
            user.setUserId(userId);
            return user;
        }

        private List<UserDocument> getListWithUser(String userId) {
            return List.of(
                    getTestUserDocument(),
                    getTestUserDocument(),
                    getTestUserDocument(userId)
            );
        }

        private List<UserDocument> getListWithoutUser() {
            return List.of(
                    getTestUserDocument(),
                    getTestUserDocument(),
                    getTestUserDocument()
            );
        }

        @Nested
        class WhenRemoveFromBasicList {
            @Test
            void shouldRemoveFromBasicListWhenPresent() {
                String userId = UUID.randomUUID().toString();
                List<UserDocument> basicList = getListWithUser(userId);
                groupTraining.setBasicList(basicList);
                UserDocument user = getTestUserDocument(userId);

                removeFromBasicList(groupTraining, userId);

                assertThat(groupTraining.getBasicList().contains(user)).isFalse();
                assertThat(groupTraining.getBasicList()).isNotEqualTo(basicList);
            }

            @Test
            void shouldChangeBasicListWhenInvalidIdProvided() {
                String userId = UUID.randomUUID().toString();
                List<UserDocument> basicList = getListWithoutUser();
                groupTraining.setBasicList(basicList);
                UserDocument user = getTestUserDocument(userId);

                removeFromBasicList(groupTraining, userId);

                assertThat(groupTraining.getBasicList().contains(user)).isFalse();
                assertThat(groupTraining.getBasicList()).isEqualTo(basicList);
            }

            @Test
            void shouldChangeBasicListWhenNullIdProvided() {
                List<UserDocument> basicList = getListWithoutUser();
                groupTraining.setBasicList(basicList);
                UserDocument user = getTestUserDocument(null);

                removeFromBasicList(groupTraining, null);

                assertThat(groupTraining.getBasicList().contains(user)).isFalse();
                assertThat(groupTraining.getBasicList()).isEqualTo(basicList);
            }
        }

        @Nested
        class WhenRemoveFromReserveList {
            @Test
            void shouldRemoveFromBasicListWhenPresent() {
                String userId = UUID.randomUUID().toString();
                List<UserDocument> reserveList = getListWithUser(userId);
                groupTraining.setReserveList(reserveList);
                UserDocument user = getTestUserDocument(userId);

                removeFromReserveList(groupTraining, userId);

                assertThat(groupTraining.getReserveList().contains(user)).isFalse();
                assertThat(groupTraining.getReserveList()).isNotEqualTo(reserveList);
            }

            @Test
            void shouldChangeBasicListWhenInvalidIdProvided() {
                String userId = UUID.randomUUID().toString();
                List<UserDocument> reserveList = getListWithoutUser();
                groupTraining.setReserveList(reserveList);
                UserDocument user = getTestUserDocument(userId);

                removeFromReserveList(groupTraining, userId);

                assertThat(groupTraining.getReserveList().contains(user)).isFalse();
                assertThat(groupTraining.getReserveList()).isEqualTo(reserveList);
            }

            @Test
            void shouldChangeBasicListWhenNullIdProvided() {
                List<UserDocument> reserveList = getListWithoutUser();
                groupTraining.setReserveList(reserveList);
                UserDocument user = getTestUserDocument(null);

                removeFromReserveList(groupTraining, null);

                assertThat(groupTraining.getReserveList().contains(user)).isFalse();
                assertThat(groupTraining.getReserveList()).isEqualTo(reserveList);
            }
        }
    }


}