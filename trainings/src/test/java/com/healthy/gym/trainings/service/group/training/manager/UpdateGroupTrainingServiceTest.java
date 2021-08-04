package com.healthy.gym.trainings.service.group.training.manager;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.GroupTrainingDocumentUpdateBuilder;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingServiceImpl;
import com.healthy.gym.trainings.shared.BasicUserInfoDTO;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UpdateGroupTrainingServiceTest {

    private GroupTrainingsDAO groupTrainingsDAO;
    private ManagerGroupTrainingRequest groupTrainingRequest;
    private ManagerGroupTrainingService managerGroupTrainingService;
    private GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder;
    private String groupTrainingId;

    @BeforeEach
    void setUp() {
        groupTrainingId = UUID.randomUUID().toString();
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        TrainingTypeDAO trainingTypeDAO = mock(TrainingTypeDAO.class);
        LocationDAO locationDAO = mock(LocationDAO.class);
        UserDAO userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        groupTrainingDocumentUpdateBuilder = mock(GroupTrainingDocumentUpdateBuilder.class, RETURNS_DEEP_STUBS);
        groupTrainingRequest = getGroupTrainingRequest();

        managerGroupTrainingService = new ManagerGroupTrainingServiceImpl(
                groupTrainingsDAO,
                trainingTypeDAO,
                locationDAO,
                userDAO,
                clock,
                groupTrainingDocumentUpdateBuilder
        );
    }

    private ManagerGroupTrainingRequest getGroupTrainingRequest() {
        ManagerGroupTrainingRequest createGroupTrainingRequest = new ManagerGroupTrainingRequest();
        createGroupTrainingRequest.setTrainingTypeId("122ed953-e37f-435a-bd1e-9fb2a327c4d3");
        createGroupTrainingRequest.setTrainerIds(
                List.of("100ed952-es7f-435a-bd1e-9fb2a327c4dk", "501692e9-2a79-46bb-ac62-55f980581bad")
        );
        createGroupTrainingRequest.setStartDate("2021-07-10T20:00");
        createGroupTrainingRequest.setEndDate("2021-07-10T21:00");
        createGroupTrainingRequest.setLocationId("05cbccea-6248-4e40-931b-a34031a8c678");
        createGroupTrainingRequest.setLimit(10);
        return createGroupTrainingRequest;
    }

    @Test
    void shouldThrowNotExistingGroupTrainingException() {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, null)
        ).isInstanceOf(NotExistingGroupTrainingException.class);
    }

    @Test
    void shouldThrowStartDateAfterEndDateException() throws
            TrainingTypeNotFoundException,
            PastDateException,
            LocationNotFoundException,
            TrainerNotFoundException {

        GroupTrainingDocument groupTrainingUpdated = new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T22:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                List.of(getTestUserDocument1()),
                List.of(getTestUserDocument2())
        );

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(groupTrainingUpdated);
        when(groupTrainingsDAO.save(any())).thenReturn(groupTrainingUpdated);

        assertThatThrownBy(
                () -> managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest)
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldProperlySaveAndReturnGroupTrainingDTO() throws TrainingTypeNotFoundException,
            LocationOccupiedException,
            PastDateException,
            LocationNotFoundException,
            NotExistingGroupTrainingException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException {

        GroupTrainingDocument groupTrainingUpdated = new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T20:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                List.of(getTestUserDocument1()),
                List.of(getTestUserDocument2())
        );

        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(new GroupTrainingDocument());
        when(groupTrainingDocumentUpdateBuilder
                .setGroupTrainingDocumentToUpdate(any())
                .setGroupTrainingRequest(any())
                .updateTrainingType()
                .updateTrainers()
                .updateStartDate()
                .updateEndDate()
                .updateLocation()
                .updateLimit()
                .update()
        ).thenReturn(groupTrainingUpdated);
        when(groupTrainingsDAO.save(any())).thenReturn(groupTrainingUpdated);

        assertThat(managerGroupTrainingService.updateGroupTraining(groupTrainingId, groupTrainingRequest))
                .isEqualTo(getExpectedGroupTrainingDTO());
    }

    private TrainingTypeDocument getTestTrainingTypeDocument() {
        return new TrainingTypeDocument(
                "122ed953-e37f-435a-bd1e-9fb2a327c4d3",
                "TestTraining",
                "TestDescription",
                null,
                null
        );
    }

    private UserDocument getTestTrainer1() {
        var user = new UserDocument();
        user.setName("TestTrainerName1");
        user.setSurname("TestTrainerSurname1");
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private UserDocument getTestTrainer2() {
        var user = new UserDocument();
        user.setName("TestTrainerName2");
        user.setSurname("TestTrainerSurname2");
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }

    private LocationDocument getTestLocationDocument() {
        return new LocationDocument("f0001d12-a79a-4b8d-a78d-f71c9726a6d9", "TestLocation");
    }

    private UserDocument getTestUserDocument1() {
        var user = new UserDocument();
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setName("TestName1");
        user.setSurname("TestSurname1");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private UserDocument getTestUserDocument2() {
        var user = new UserDocument();
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setName("TestName2");
        user.setSurname("TestSurname2");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private GroupTrainingDTO getExpectedGroupTrainingDTO() {
        var trainer1 = new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestTrainerName1",
                "TestTrainerSurname1",
                null
        );

        var trainer2 = new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestTrainerName2",
                "TestTrainerSurname2",
                null
        );

        var user1 = new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestName1",
                "TestSurname1",
                null
        );

        var user2 = new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestName2",
                "TestSurname2",
                null
        );

        var groupTrainingDTO = new GroupTrainingDTO(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                "TestTraining",
                "2021-07-10T20:00",
                "2021-07-10T21:00",
                false,
                "TestLocation",
                List.of(trainer1, trainer2)
        );
        groupTrainingDTO.setBasicList(List.of(user1));
        groupTrainingDTO.setReserveList(List.of(user2));

        return groupTrainingDTO;
    }
}
