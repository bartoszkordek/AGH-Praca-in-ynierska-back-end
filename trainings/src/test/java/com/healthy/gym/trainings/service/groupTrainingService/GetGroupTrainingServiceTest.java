package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.GroupTrainingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GetGroupTrainingServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldReturnAllGroupTrainings_whenValidRequest() throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;

        double rating = 0.0;
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTraining = new GroupTrainingResponse(trainingId, trainingName, trainerId, date, startTime,
                endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(groupTrainingsDbRepository.getGroupTrainings(startDate, endDate)).thenReturn(groupTrainings);

        //then
        assertThat(groupTrainingService.getGroupTrainings(startDate, endDate)).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnAllGroupTrainingsPublicView_whenValidRequest() throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        double rating = 0.0;

        GroupTrainingPublicResponse groupTrainingPublicResponse = new GroupTrainingPublicResponse(trainingId,
                trainingName, trainerId, date, startTime, endTime, hallNo, limit, rating);

        List<GroupTrainingPublicResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTrainingPublicResponse);

        //when
        when(groupTrainingsDbRepository.getPublicGroupTrainings(startDate, endDate)).thenReturn(groupTrainings);

        //then
        assertThat(groupTrainingService.getPublicGroupTrainings(startDate, endDate)).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnGroupTrainingByTrainingId_whenValidRequest() throws InvalidHourException, NotExistingGroupTrainingException, InvalidDateException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        double rating = 0.0;

        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTraining = new GroupTrainingResponse(trainingId, trainingName, trainerId, date, startTime,
                endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.getGroupTrainingById(trainingId)).thenReturn(groupTraining);

        //then
        assertThat(groupTrainingService.getGroupTrainingById(trainingId)).isEqualTo(groupTraining);
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotReturnGroupTrainingByTrainingId_whenInvalidTrainingId() throws InvalidHourException, NotExistingGroupTrainingException, InvalidDateException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String invalidTrainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(invalidTrainingId)).thenReturn(false);

        //then
        groupTrainingService.getGroupTrainingById(invalidTrainingId);
    }

    @Test
    public void shouldGetTrainingParticipants_whenValidTrainingId() throws NotExistingGroupTrainingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String participant1UserId = "222ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String participant1Name = "John";
        String participant1Surname = "Smith";
        String participant2UserId = "222ed953-e37f-435a-bd1e-9fb2a327c4d4";
        String participant2Name = "Max";
        String participant2Surname = "Adams";
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        ParticipantsResponse participants1Response = new ParticipantsResponse(participant1UserId, participant1Name,
                participant1Surname);
        ParticipantsResponse participants2Response = new ParticipantsResponse(participant2UserId, participant2Name,
                participant2Surname);
        participantsResponses.add(participants1Response);
        participantsResponses.add(participants2Response);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.getTrainingParticipants(trainingId)).thenReturn(participantsResponses);

        //then
        assertThat(groupTrainingService.getTrainingParticipants(trainingId));
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotGetTrainingParticipants_whenInvalidTrainingId() throws NotExistingGroupTrainingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String invalidTrainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(invalidTrainingId)).thenReturn(false);

        //then
        groupTrainingService.getTrainingParticipants(invalidTrainingId);
    }

    @Test
    public void shouldReturnGroupTrainingsByTrainingTypeId_whenValidRequest() throws InvalidDateException, InvalidHourException, ParseException, StartDateAfterEndDateException, NotExistingGroupTrainingException, TrainingTypeNotFoundException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        double rating = 0.0;

        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTraining = new GroupTrainingResponse(trainingId, trainingName, trainerId, date, startTime,
                endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)).thenReturn(true);
        when(groupTrainingsDbRepository.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate)).thenReturn(groupTrainings);

        //then
        assertThat(groupTrainingService.getGroupTrainingsByType(trainingTypeId, startDate, endDate)).isEqualTo(groupTrainings);
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnGroupTrainings_whenInvalidTrainingTypeId() throws InvalidDateException, InvalidHourException, StartDateAfterEndDateException, ParseException, NotExistingGroupTrainingException, TrainingTypeNotFoundException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        String date = "2020-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        double rating = 0.0;

        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTraining = new GroupTrainingResponse(trainingId, trainingName, trainerId, date, startTime,
                endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(false);
        when(groupTrainingsDbRepository.isGroupTrainingExistByType(trainingTypeId)).thenReturn(false);
        when(groupTrainingsDbRepository.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate)).thenReturn(groupTrainings);

        //then
        groupTrainingService.getGroupTrainingsByType(trainingTypeId, startDate, endDate);
    }

}
