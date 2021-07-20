package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.GroupTrainingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UpdateGroupTrainingService {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldUpdateGroupTraining_whenValidRequest() throws InvalidDateException, InvalidHourException, TrainingCreationException, ParseException, TrainingUpdateException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTimeBeforeUpdate = "18:00";
        String endTimeBeforeUpdate = "19:00";
        int hallNoBeforeUpdate = 1;
        int limitBeforeUpdate = 15;
        String startTimeAfterUpdate = "19:00";
        String endTimeAfterUpdate = "20:00";
        int hallNoAfterUpdate = 2;
        int limitAfterUpdate = 20;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate, participants,
                reserveList);
        groupTrainingBeforeUpdate.setId(id);

        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);
        groupTrainingAfterUpdate.setId(id);
        GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainerId, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participants, reserveList);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.updateTraining(trainingId,groupTrainingUpdateRequest))
                .thenReturn(groupTrainingAfterUpdate);

        //then
        assertThat(groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest))
                .isEqualTo(groupTrainingResponseAfterUpdate);
    }

    @Test(expected = TrainingUpdateException.class)
    public void shouldNotUpdateGroupTraining_whenInvalidTrainingId() throws InvalidDateException, InvalidHourException, TrainingCreationException, ParseException, TrainingUpdateException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTimeBeforeUpdate = "18:00";
        String endTimeBeforeUpdate = "19:00";
        int hallNoBeforeUpdate = 1;
        int limitBeforeUpdate = 15;
        String startTimeAfterUpdate = "19:00";
        String endTimeAfterUpdate = "20:00";
        int hallNoAfterUpdate = 2;
        int limitAfterUpdate = 20;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate, participants,
                reserveList);
        groupTrainingBeforeUpdate.setId(id);
        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);
        groupTrainingAfterUpdate.setId(id);
        GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainerId, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participants, reserveList);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(false);
        when(groupTrainingsDbRepository.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest)).thenReturn(true);
        when(groupTrainingsDbRepository.updateTraining(trainingId,groupTrainingUpdateRequest))
                .thenReturn(groupTrainingAfterUpdate);

        //then
        groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest);
    }

    @Test(expected = TrainingUpdateException.class)
    public void shouldNotUpdateGroupTraining_whenConflictWithOtherEvent() throws InvalidDateException, InvalidHourException, TrainingCreationException, ParseException, TrainingUpdateException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id= "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTimeBeforeUpdate = "18:00";
        String endTimeBeforeUpdate = "19:00";
        int hallNoBeforeUpdate = 1;
        int limitBeforeUpdate = 15;
        String startTimeAfterUpdate = "19:00";
        String endTimeAfterUpdate = "20:00";
        int hallNoAfterUpdate = 2;
        int limitAfterUpdate = 20;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1,0,0,0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate, participants,
                reserveList);
        groupTrainingBeforeUpdate.setId(id);
        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);
        groupTrainingAfterUpdate.setId(id);
        GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainerId, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participants, reserveList);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest)).thenReturn(false);
        when(groupTrainingsDbRepository.updateTraining(trainingId,groupTrainingUpdateRequest))
                .thenReturn(groupTrainingAfterUpdate);

        //then
        groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest);
    }
}
