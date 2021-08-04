package com.healthy.gym.trainings.service.group.training.manager.service.unit.tests;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RemoveGroupTrainingServiceTest {

    private GroupTrainingsDAO groupTrainingsDAO;

    private ManagerGroupTrainingService managerGroupTrainingService;
    private String groupTrainingId;

    @BeforeEach
    void setUp() {
        groupTrainingId = UUID.randomUUID().toString();
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        TrainingTypeDAO trainingTypeDAO = mock(TrainingTypeDAO.class);
        LocationDAO locationDAO = mock(LocationDAO.class);
        UserDAO userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder
                = mock(GroupTrainingDocumentUpdateBuilder.class);
        managerGroupTrainingService = new ManagerGroupTrainingServiceImpl(
                groupTrainingsDAO,
                trainingTypeDAO,
                locationDAO,
                userDAO,
                clock,
                groupTrainingDocumentUpdateBuilder
        );
    }

    @Test
    void shouldThrowNotExistingGroupTrainingException() {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString())).thenReturn(null);

        assertThatThrownBy(
                () -> managerGroupTrainingService.removeGroupTraining(groupTrainingId)
        ).isInstanceOf(NotExistingGroupTrainingException.class);
    }

    @Test
    void shouldRemoveGroupTraining() throws NotExistingGroupTrainingException {
        when(groupTrainingsDAO.findFirstByGroupTrainingId(anyString()))
                .thenReturn(getGroupTrainingDocument());

        GroupTrainingDTO groupTrainingDTO = getExpectedGroupTrainingDTO();

        assertThat(managerGroupTrainingService.removeGroupTraining("bcbc63a7-1208-42af-b1a2-134a82fb5233"))
                .isEqualTo(groupTrainingDTO);
    }

    private GroupTrainingDocument getGroupTrainingDocument() {
        return new GroupTrainingDocument(
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
        return new LocationDocument(UUID.randomUUID().toString(), "TestLocation");
    }

    private UserDocument getTestUserDocument1() {
        var user = new UserDocument();
        user.setName("TestName1");
        user.setSurname("TestSurname1");
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private UserDocument getTestUserDocument2() {
        var user = new UserDocument();
        user.setName("TestName2");
        user.setSurname("TestSurname2");
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private GroupTrainingDTO getExpectedGroupTrainingDTO() {
        var groupTrainingDto = new GroupTrainingDTO();
        groupTrainingDto.setGroupTrainingId("bcbc63a7-1208-42af-b1a2-134a82fb5233");
        groupTrainingDto.setTitle("TestTraining");
        groupTrainingDto.setStartDate("2021-07-10T20:00");
        groupTrainingDto.setEndDate("2021-07-10T21:00");
        groupTrainingDto.setLocation("TestLocation");
        groupTrainingDto.setTrainers(List.of(getTestTrainer1DTO(), getTestTrainer2DTO()));
        groupTrainingDto.setBasicList(List.of(getTestUserDocument1DTO()));
        groupTrainingDto.setReserveList(List.of(getTestUserDocument2DTO()));

        return groupTrainingDto;
    }

    private BasicUserInfoDTO getTestTrainer1DTO() {
        return new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestTrainerName1",
                "TestTrainerSurname1",
                null
        );
    }

    private BasicUserInfoDTO getTestTrainer2DTO() {
        return new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestTrainerName2",
                "TestTrainerSurname2",
                null
        );
    }

    private BasicUserInfoDTO getTestUserDocument1DTO() {
        return new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestName1",
                "TestSurname1",
                null
        );
    }

    private BasicUserInfoDTO getTestUserDocument2DTO() {
        return new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestName2",
                "TestSurname2",
                null
        );
    }
}
