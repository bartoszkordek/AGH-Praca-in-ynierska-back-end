package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupTrainingDocumentUpdaterTest {

    private TrainingTypeDAO trainingTypeDAO;
    private LocationDAO locationDAO;
    private UserDAO userDAO;

    private GroupTrainingDocument currentGroupTrainingDocument;
    private ManagerGroupTrainingRequest groupTrainingRequest;
    private GroupTrainingDocumentUpdater groupTrainingDocumentUpdater;

    @BeforeEach
    void setUp() {
        trainingTypeDAO = mock(TrainingTypeDAO.class);
        locationDAO = mock(LocationDAO.class);
        userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T12:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        currentGroupTrainingDocument = getCurrentTestGroupTrainingDocument();
        groupTrainingRequest = getTestGroupTrainingRequest();

        groupTrainingDocumentUpdater =
                new GroupTrainingDocumentUpdaterImpl(trainingTypeDAO, locationDAO, userDAO, clock);
    }

    private GroupTrainingDocument getCurrentTestGroupTrainingDocument() {
        return new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getCurrentTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T20:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private TrainingTypeDocument getCurrentTestTrainingTypeDocument() {
        return new TrainingTypeDocument(
                "5cf52114-7125-4426-bdc1-a689973e3d1e",
                "TestTrainingOld",
                "TestDescriptionOld",
                null,
                null
        );
    }

    private UserDocument getTestTrainer1() {
        var user = new UserDocument();
        user.setName("TestName1");
        user.setSurname("TestSurname1");
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private UserDocument getTestTrainer2() {
        var user = new UserDocument();
        user.setName("TestName2");
        user.setSurname("TestSurname2");
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private LocationDocument getTestLocationDocument() {
        return new LocationDocument("d5206830-c2d5-4012-a04a-3df5c25fb62a", "TestLocation");
    }

    private ManagerGroupTrainingRequest getTestGroupTrainingRequest() {
        ManagerGroupTrainingRequest createGroupTrainingRequest = new ManagerGroupTrainingRequest();
        createGroupTrainingRequest.setTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3");
        createGroupTrainingRequest.setTrainerIds(List.of("dd6be548-86a2-47d9-896f-290e9752c216"));
        createGroupTrainingRequest.setStartDate("2021-07-10T18:30");
        createGroupTrainingRequest.setEndDate("2021-07-10T22:00");
        createGroupTrainingRequest.setLocationId("f47d1d59-787c-4d49-8daf-37e5704d8ed2");
        createGroupTrainingRequest.setLimit(5);
        return createGroupTrainingRequest;
    }

    @Nested
    class GroupTrainingDocumentAndGroupTrainingRequestAreSet {

        private TrainingTypeDocument trainingTypeDocumentUpdated;
        private UserDocument trainerUpdated;
        private LocationDocument location;

        @BeforeEach
        void setUp() {
            trainingTypeDocumentUpdated = new TrainingTypeDocument(
                    "122ed953-e37f-435a-bd1e-9fb2a327c4d3",
                    "TestTraining",
                    "TestDescription",
                    null,
                    null
            );
            trainerUpdated = getTrainerUpdated();
            location = new LocationDocument(
                    "f47d1d59-787c-4d49-8daf-37e5704d8ed2",
                    "NewLocation"
            );
        }

        private UserDocument getTrainerUpdated() {
            var trainerUpdated = new UserDocument();
            trainerUpdated.setName("TestName2");
            trainerUpdated.setSurname("TestSurname2");
            trainerUpdated.setUserId("dd6be548-86a2-47d9-896f-290e9752c216");
            trainerUpdated.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
            return trainerUpdated;
        }

        @Test
        void shouldUpdateTrainingType() throws TrainingTypeNotFoundException {
            when(trainingTypeDAO.findByTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3"))
                    .thenReturn(trainingTypeDocumentUpdated);

            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateTrainingType()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    trainingTypeDocumentUpdated,
                                    List.of(getTestTrainer1(), getTestTrainer2()),
                                    LocalDateTime.parse("2021-07-10T20:00"),
                                    LocalDateTime.parse("2021-07-10T21:00"),
                                    getTestLocationDocument(),
                                    10,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldThrowTrainingTypeNotFoundException() {
            when(trainingTypeDAO.findByTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3"))
                    .thenReturn(null);

            assertThatThrownBy(
                    () -> groupTrainingDocumentUpdater
                            .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                            .setGroupTrainingRequest(groupTrainingRequest)
                            .updateTrainingType()
                            .update()
            ).isInstanceOf(TrainingTypeNotFoundException.class);
        }

        @Test
        void shouldUpdateTrainers() throws TrainerNotFoundException {
            when(userDAO.findByUserId("dd6be548-86a2-47d9-896f-290e9752c216"))
                    .thenReturn(trainerUpdated);

            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateTrainers()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    getCurrentTestTrainingTypeDocument(),
                                    List.of(trainerUpdated),
                                    LocalDateTime.parse("2021-07-10T20:00"),
                                    LocalDateTime.parse("2021-07-10T21:00"),
                                    getTestLocationDocument(),
                                    10,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldThrowTrainerNotFoundException() {
            when(userDAO.findByUserId("dd6be548-86a2-47d9-896f-290e9752c216")).thenReturn(null);

            assertThatThrownBy(
                    () -> groupTrainingDocumentUpdater
                            .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                            .setGroupTrainingRequest(groupTrainingRequest)
                            .updateTrainers()
                            .update()
            ).isInstanceOf(TrainerNotFoundException.class);
        }

        @Test
        void shouldUpdateStartDate() throws PastDateException {
            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateStartDate()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    getCurrentTestTrainingTypeDocument(),
                                    List.of(getTestTrainer1(), getTestTrainer2()),
                                    LocalDateTime.parse("2021-07-10T18:30"),
                                    LocalDateTime.parse("2021-07-10T21:00"),
                                    getTestLocationDocument(),
                                    10,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldThrowPastDateExceptionWhenUpdatingStartDate() {
            groupTrainingRequest.setStartDate("2021-07-10T08:00");

            assertThatThrownBy(
                    () -> groupTrainingDocumentUpdater
                            .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                            .setGroupTrainingRequest(groupTrainingRequest)
                            .updateStartDate()
                            .update()
            ).isInstanceOf(PastDateException.class);
        }


        @Test
        void shouldUpdateEndDate() throws PastDateException {
            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateEndDate()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    getCurrentTestTrainingTypeDocument(),
                                    List.of(getTestTrainer1(), getTestTrainer2()),
                                    LocalDateTime.parse("2021-07-10T20:00"),
                                    LocalDateTime.parse("2021-07-10T22:00"),
                                    getTestLocationDocument(),
                                    10,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldThrowPastDateExceptionWhenUpdatingEndDate() {
            groupTrainingRequest.setEndDate("2021-07-10T08:00");

            assertThatThrownBy(
                    () -> groupTrainingDocumentUpdater
                            .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                            .setGroupTrainingRequest(groupTrainingRequest)
                            .updateEndDate()
                            .update()
            ).isInstanceOf(PastDateException.class);
        }

        @Test
        void shouldUpdateLocation() throws LocationNotFoundException {
            when(locationDAO.findByLocationId("f47d1d59-787c-4d49-8daf-37e5704d8ed2")).thenReturn(location);

            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateLocation()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    getCurrentTestTrainingTypeDocument(),
                                    List.of(getTestTrainer1(), getTestTrainer2()),
                                    LocalDateTime.parse("2021-07-10T20:00"),
                                    LocalDateTime.parse("2021-07-10T21:00"),
                                    location,
                                    10,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldThrowLocationNotFoundException() {
            when(locationDAO.findByLocationId(anyString())).thenReturn(null);

            assertThatThrownBy(() -> groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateLocation()
                    .update()
            ).isInstanceOf(LocationNotFoundException.class);
        }

        @Test
        void shouldUpdateLimit() {
            groupTrainingRequest.setLimit(9);

            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateLimit()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    getCurrentTestTrainingTypeDocument(),
                                    List.of(getTestTrainer1(), getTestTrainer2()),
                                    LocalDateTime.parse("2021-07-10T20:00"),
                                    LocalDateTime.parse("2021-07-10T21:00"),
                                    getTestLocationDocument(),
                                    9,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }

        @Test
        void shouldUpdateWholeGroupTrainingDocument()
                throws TrainingTypeNotFoundException,
                TrainerNotFoundException,
                PastDateException,
                LocationNotFoundException {

            when(trainingTypeDAO.findByTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3"))
                    .thenReturn(trainingTypeDocumentUpdated);
            when(userDAO.findByUserId("dd6be548-86a2-47d9-896f-290e9752c216"))
                    .thenReturn(trainerUpdated);
            when(locationDAO.findByLocationId("f47d1d59-787c-4d49-8daf-37e5704d8ed2"))
                    .thenReturn(location);

            GroupTrainingDocument updatedGroupTrainingDocument = groupTrainingDocumentUpdater
                    .setGroupTrainingDocumentToUpdate(currentGroupTrainingDocument)
                    .setGroupTrainingRequest(groupTrainingRequest)
                    .updateTrainingType()
                    .updateTrainers()
                    .updateStartDate()
                    .updateEndDate()
                    .updateLocation()
                    .updateLimit()
                    .update();

            assertThat(updatedGroupTrainingDocument)
                    .isEqualTo(
                            new GroupTrainingDocument(
                                    "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                                    trainingTypeDocumentUpdated,
                                    List.of(trainerUpdated),
                                    LocalDateTime.parse("2021-07-10T18:30"),
                                    LocalDateTime.parse("2021-07-10T22:00"),
                                    location,
                                    5,
                                    new ArrayList<>(),
                                    new ArrayList<>()
                            )
                    );
        }
    }

    @Nested
    class NeitherGroupTrainingDocumentNorGroupTrainingRequestIsSet {

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateTrainingType() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateTrainingType())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateTrainers() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateTrainers())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateStartDate() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateStartDate())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateEndDate() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateEndDate())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateLocation() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateLocation())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdateLimit() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.updateLimit())
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenUpdate() {
            assertThatThrownBy(() -> groupTrainingDocumentUpdater.update())
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}