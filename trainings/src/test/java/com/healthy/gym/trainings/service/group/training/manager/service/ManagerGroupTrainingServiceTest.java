package com.healthy.gym.trainings.service.group.training.manager.service;

import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.LocationDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.data.repository.UserDAO;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class ManagerGroupTrainingServiceTest {

    @Autowired
    private ManagerGroupTrainingService managerGroupTrainingService;

    @MockBean
    private GroupTrainingsDAO groupTrainingsDAO;

    @MockBean
    private TrainingTypeDAO trainingTypeDAO;

    @MockBean
    private LocationDAO locationDAO;

    @MockBean
    private UserDAO userDAO;

    private ManagerGroupTrainingRequest request;

    @BeforeEach
    void setUp() {
        request = new ManagerGroupTrainingRequest();
        request.setTrainingTypeId(UUID.randomUUID().toString());
        request.setTrainerIds(List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        request.setStartDate("2021-12-10T19:00");
        request.setEndDate("2021-12-10T20:00");
        request.setLocationId(UUID.randomUUID().toString());
        request.setLimit(10);
    }

    @Test
    void shouldThrowTrainingTypeNotFoundException() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(request))
                .isInstanceOf(TrainingTypeNotFoundException.class);
    }

    @Test
    void shouldThrowTrainerNotFoundExceptionWhenUserDoNotHaveTrainerRole() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(new TrainingTypeDocument());
        UserDocument userDocument = new UserDocument();
        userDocument.setGymRoles(List.of(GymRole.USER));
        when(userDAO.findByUserId(anyString())).thenReturn(userDocument);

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(request))
                .isInstanceOf(TrainerNotFoundException.class);
    }

    @Test
    void shouldThrowStartDateAfterEndDateException() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(new TrainingTypeDocument());
        UserDocument userDocument = new UserDocument();
        userDocument.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        when(userDAO.findByUserId(anyString())).thenReturn(userDocument);
        when(locationDAO.findByLocationId(anyString())).thenReturn(new LocationDocument());

        request.setStartDate("2021-12-10T21:00");

        assertThatThrownBy(() -> managerGroupTrainingService.createGroupTraining(request))
                .isInstanceOf(StartDateAfterEndDateException.class);
    }
}