package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.GroupTrainingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GetGroupTrainingServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldReturnAllGroupTrainings_whenValidRequest() throws InvalidHourException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository);

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        List<GroupTrainings> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(groupTrainingsDbRepository.getGroupTrainings()).thenReturn(groupTrainings);

        //then
        assertThat(groupTrainingService.getGroupTrainings()).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnAllGroupTrainingsPublicView_whenValidRequest() throws InvalidHourException, InvalidDateException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository);

        //before
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);
        GroupTrainingPublicResponse groupTrainingPublicResponse = new GroupTrainingPublicResponse(trainingId,
                trainingTypeId, trainerId, date, startTime, endTime, hallNo, limit);

        List<GroupTrainingPublicResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTrainingPublicResponse);

        //when
        when(groupTrainingsDbRepository.getPublicGroupTrainings()).thenReturn(groupTrainings);

        //then
        assertThat(groupTrainingService.getPublicGroupTrainings()).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnGroupTrainingByTrainingId_whenValidRequest() throws InvalidHourException, NotExistingGroupTrainingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository);

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);
        groupTraining.setId(id);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.getGroupTrainingById(trainingId)).thenReturn(groupTraining);

        //then
        assertThat(groupTrainingService.getGroupTrainingById(trainingId)).isEqualTo(groupTraining);
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotReturnGroupTrainingByTrainingId_whenInvalidTrainingId() throws InvalidHourException, NotExistingGroupTrainingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository);

        //before
        String invalidTrainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(invalidTrainingId)).thenReturn(false);

        //then
        groupTrainingService.getGroupTrainingById(invalidTrainingId);
    }


}
