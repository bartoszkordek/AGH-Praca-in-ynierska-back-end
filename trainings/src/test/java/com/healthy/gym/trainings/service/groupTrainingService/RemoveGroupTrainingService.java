package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.service.group.training.GroupTrainingService;
import com.healthy.gym.trainings.service.group.training.GroupTrainingServiceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RemoveGroupTrainingService {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldRemoveGroupTraining_whenTrainingExists() throws InvalidDateException, InvalidHourException, TrainingRemovalException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;

        List<UserDocument> trainersDocuments = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1Email = "sample@trainer.com";
        String trainer1PhoneNumber = "666222333";
        String trainer1EncryptedPassword = "encrypted_password123!";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserDocument trainer1Document = new UserDocument(
                trainer1Name,
                trainer1Surname,
                trainer1Email,
                trainer1PhoneNumber,
                trainer1EncryptedPassword,
                trainer1UserId);
        trainer1Document.setId("507f191e810c19729de860ea");
        trainersDocuments.add(trainer1Document);

        List<UserDocument> participantDocuments = new ArrayList<>();
        List<UserDocument> reserveListDocuments = new ArrayList<>();

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1, 0, 0, 0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTime, endTime, hallNo, limit, participantDocuments, reserveListDocuments);
        groupTraining.setId(id);

        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTime, endTime, hallNo, limit, rating, participantsResponses,
                reserveListResponses);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepository.removeTraining(trainingId)).thenReturn(groupTraining);

        //then
        //TODO
        //assertThat(groupTrainingService.removeGroupTraining(trainingId)).isEqualTo(groupTrainingResponse);
    }

    @Ignore
    @Test(expected = TrainingRemovalException.class)
    public void shouldNotRemoveGroupTraining_whenInvalidTrainingId() throws InvalidDateException, InvalidHourException, TrainingRemovalException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepository groupTrainingsDbRepository = Mockito.mock(GroupTrainingsDbRepository.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(emailConfig, groupTrainingsDbRepository,
                trainingTypeRepository);

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;

        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainersIds, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1, 0, 0, 0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        List<UserDocument> trainersDocuments = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1Email = "sample@trainer.com";
        String trainer1PhoneNumber = "666222333";
        String trainer1EncryptedPassword = "encrypted_password123!";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserDocument trainer1Document = new UserDocument(
                trainer1Name,
                trainer1Surname,
                trainer1Email,
                trainer1PhoneNumber,
                trainer1EncryptedPassword,
                trainer1UserId);
        trainer1Document.setId("507f191e810c19729de860ea");
        trainersDocuments.add(trainer1Document);

        List<UserDocument> participantDocuments = new ArrayList<>();
        List<UserDocument> reserveListDocuments = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTime, endTime, hallNo, limit, participantDocuments, reserveListDocuments);
        groupTraining.setId(id);

        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTime, endTime, hallNo, limit, rating, participantsResponses,
                reserveListResponses);

        //when
        when(groupTrainingsDbRepository.isGroupTrainingExist(trainingId)).thenReturn(false);
        when(groupTrainingsDbRepository.removeTraining(trainingId)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.removeGroupTraining(trainingId);
    }
}
