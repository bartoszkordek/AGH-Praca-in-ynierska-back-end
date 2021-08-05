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
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.GroupTrainingDocumentUpdateBuilder;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingServiceImpl;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateGroupTrainingServiceTest {

    private GroupTrainingsDAO groupTrainingsDAO;
    private TrainingTypeDAO trainingTypeDAO;
    private LocationDAO locationDAO;
    private UserDAO userDAO;

    private ManagerGroupTrainingRequest createGroupTrainingRequest;
    private ManagerGroupTrainingService managerGroupTrainingService;

    @BeforeEach
    void setUp() {
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        trainingTypeDAO = mock(TrainingTypeDAO.class);
        locationDAO = mock(LocationDAO.class);
        userDAO = mock(UserDAO.class);
        Clock clock = Clock.fixed(Instant.parse("2021-07-10T18:00:00.00Z"), ZoneId.of("Europe/Warsaw"));

        GroupTrainingDocumentUpdateBuilder groupTrainingDocumentUpdateBuilder = mock(GroupTrainingDocumentUpdateBuilder.class);
        createGroupTrainingRequest = getCreateGroupTrainingRequest();
        managerGroupTrainingService = new ManagerGroupTrainingServiceImpl(
                groupTrainingsDAO,
                trainingTypeDAO,
                locationDAO,
                userDAO,
                clock,
                groupTrainingDocumentUpdateBuilder
        );
    }

    private ManagerGroupTrainingRequest getCreateGroupTrainingRequest() {
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
    void shouldThrowTrainingTypeNotFoundException_whenTrainingTypeNotFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(TrainingTypeNotFoundException.class);
    }

    @Test
    void shouldThrowTrainerNotFoundException_whenTrainerIsNotFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(TrainerNotFoundException.class);
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

    @Test
    void shouldThrowTrainerNotFoundException_whenNoUserHasTrainerRole() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk")).thenReturn(getTestUserDocument1());
        when(userDAO.findByUserId("501692e9-2a79-46bb-ac62-55f980581bad")).thenReturn(getTestUserDocument2());

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(TrainerNotFoundException.class);
    }

    private UserDocument getTestUserDocument1() {
        var user = new UserDocument();
        user.setUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    private UserDocument getTestUserDocument2() {
        var user = new UserDocument();
        user.setUserId("501692e9-2a79-46bb-ac62-55f980581bad");
        user.setGymRoles(List.of(GymRole.USER));
        return user;
    }

    @Test
    void shouldThrowLocationNotFoundException_whenLocationIsNotFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk")).thenReturn(getTestTrainer1());
        when(userDAO.findByUserId("501692e9-2a79-46bb-ac62-55f980581bad")).thenReturn(getTestTrainer2());
        when(locationDAO.findByLocationId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(LocationNotFoundException.class);
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

    @Test
    void shouldThrowPastDateException_whenProvidedStartTimeIsInThePast() {
        createGroupTrainingRequest.setStartDate("2021-07-09T20:00");

        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk")).thenReturn(getTestTrainer1());
        when(userDAO.findByUserId("501692e9-2a79-46bb-ac62-55f980581bad")).thenReturn(getTestTrainer2());
        when(locationDAO.findByLocationId(anyString())).thenReturn(getTestLocationDocument());

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(PastDateException.class);
    }

    private LocationDocument getTestLocationDocument() {
        return new LocationDocument(UUID.randomUUID().toString(), "TestLocation");
    }

    @Test
    void shouldThrowStartDateAfterDateException() {
        createGroupTrainingRequest.setEndDate("2021-07-10T19:59");

        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk")).thenReturn(getTestTrainer1());
        when(userDAO.findByUserId("501692e9-2a79-46bb-ac62-55f980581bad")).thenReturn(getTestTrainer2());
        when(locationDAO.findByLocationId(anyString())).thenReturn(getTestLocationDocument());

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest))
                .isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Test
    void shouldProperlySaveGroupTraining()
            throws TrainingTypeNotFoundException,
            LocationOccupiedException,
            PastDateException,
            LocationNotFoundException,
            StartDateAfterEndDateException,
            TrainerOccupiedException,
            TrainerNotFoundException {

        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(getTestTrainingTypeDocument());
        when(userDAO.findByUserId("100ed952-es7f-435a-bd1e-9fb2a327c4dk")).thenReturn(getTestTrainer1());
        when(userDAO.findByUserId("501692e9-2a79-46bb-ac62-55f980581bad")).thenReturn(getTestTrainer2());
        when(locationDAO.findByLocationId(anyString())).thenReturn(getTestLocationDocument());
        when(groupTrainingsDAO.save(any())).thenReturn(getSavedTestGroupTrainingDocument());

        assertThat(managerGroupTrainingService.createGroupTraining(createGroupTrainingRequest)).
                isEqualTo(getExpectedGroupTrainingDTO());
    }

    private GroupTrainingDocument getSavedTestGroupTrainingDocument() {
        return new GroupTrainingDocument(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                getTestTrainingTypeDocument(),
                List.of(getTestTrainer1(), getTestTrainer2()),
                LocalDateTime.parse("2021-07-10T20:00"),
                LocalDateTime.parse("2021-07-10T21:00"),
                getTestLocationDocument(),
                10,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private GroupTrainingDTO getExpectedGroupTrainingDTO() {
        var trainer1 = new BasicUserInfoDTO(
                "100ed952-es7f-435a-bd1e-9fb2a327c4dk",
                "TestName1",
                "TestSurname1",
                null
        );

        var trainer2 = new BasicUserInfoDTO(
                "501692e9-2a79-46bb-ac62-55f980581bad",
                "TestName2",
                "TestSurname2",
                null
        );

        return new GroupTrainingDTO(
                "bcbc63a7-1208-42af-b1a2-134a82fb5233",
                "TestTraining",
                "2021-07-10T20:00",
                "2021-07-10T21:00",
                false,
                "TestLocation",
                List.of(trainer1, trainer2)
        );
    }
}
