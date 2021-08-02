package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.training.TrainingCreationException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.UserResponse;
import com.healthy.gym.trainings.service.group.training.GroupTrainingService;
import com.healthy.gym.trainings.service.group.training.GroupTrainingServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class CreateGroupTrainingService {

    @Autowired
    private ApplicationContext applicationContext;

    private TrainingTypeDAO trainingTypeRepository;
    private GroupTrainingsRepository groupTrainingsRepository;
    private ReviewDAO reviewDAO;
    private GroupTrainingService groupTrainingService;

    @Before
    public void setUp() throws Exception {
        trainingTypeRepository = mock(TrainingTypeDAO.class);
        groupTrainingsRepository = mock(GroupTrainingsRepository.class);
        reviewDAO = mock(ReviewDAO.class);
        groupTrainingService = new GroupTrainingServiceImpl(
                trainingTypeRepository,
                groupTrainingsRepository,
                reviewDAO
        );
    }

    @Test
    public void shouldCreateGroupTraining_whenValidRequest()
            throws InvalidDateException, InvalidHourException {

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

        double rating = 0.0;

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


        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);
        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTime, endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //assertThat(userGroupTrainingService.createGroupTraining(groupTrainingRequest)).isEqualTo(groupTrainingResponse);
    }

    @Ignore
    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenOverlappingEvents()
            throws InvalidDateException, InvalidHourException {

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

        List<UserDocument> trainersDocuments = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1Email = "sample@trainer.com";
        String trainer1PhoneNumber = "666222333";
        String trainer1EncryptedPassword = "encrypted_password123!";
        String trainer1UserId = trainerId;
        UserDocument trainer1Document = new UserDocument(
                trainer1Name,
                trainer1Surname,
                trainer1Email,
                trainer1PhoneNumber,
                trainer1EncryptedPassword,
                trainer1UserId);
        trainer1Document.setId("507f191e810c19729de860ea");
        trainersDocuments.add(trainer1Document);

        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainersIds, date,
                startTime, endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1, 0, 0, 0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserDocument> participantDocuments = new ArrayList<>();
        List<UserDocument> reserveListDocuments = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTime, endTime, hallNo, limit, participantDocuments, reserveListDocuments);
        groupTraining.setId(id);

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(false);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Ignore
    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenRetroDate()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
        String date = "1900-01-01";
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
        String trainer1UserId = trainerId;
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

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = InvalidDateException.class)
    public void shouldNotCreateGroupTraining_whenInvalidDate()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
        String date = "1900-13-01";
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
        String trainer1UserId = trainerId;
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

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Test(expected = InvalidHourException.class)
    public void shouldNotCreateGroupTraining_whenInvalidHour()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "25:00";
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
        String trainer1UserId = trainerId;
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

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.createGroupTraining(groupTrainingRequest);
    }

    @Ignore
    @Test(expected = TrainingCreationException.class)
    public void shouldNotCreateGroupTraining_whenStartDateAfterEndDate()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
        String date = "2030-07-01";
        String startTime = "19:00";
        String endTime = "18:00";
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
        String trainer1UserId = trainerId;
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

        //when
//        when(groupTrainingsDbRepositoryImpl.isAbilityToCreateTraining(groupTrainingRequest)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.createTraining(groupTrainingRequest)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.createGroupTraining(groupTrainingRequest);
    }
}
