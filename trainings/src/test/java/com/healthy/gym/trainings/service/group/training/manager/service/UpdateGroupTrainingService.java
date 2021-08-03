package com.healthy.gym.trainings.service.group.training.manager.service;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.training.TrainingUpdateException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
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
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UpdateGroupTrainingService {

    @Autowired
    ApplicationContext applicationContext;

    private TrainingTypeDAO trainingTypeRepository;
    private GroupTrainingsRepository groupTrainingsRepository;
    private GroupTrainingsDAO groupTrainingsDAO;
    private TrainingTypeDAO trainingTypeDAO;
    private ReviewDAO reviewDAO;
    private GroupTrainingService groupTrainingService;

    @Before
    public void setUp() throws Exception {
        trainingTypeRepository = mock(TrainingTypeDAO.class);
        groupTrainingsRepository = mock(GroupTrainingsRepository.class);
        groupTrainingsDAO = mock(GroupTrainingsDAO.class);
        trainingTypeDAO = mock(TrainingTypeDAO.class);
        reviewDAO = mock(ReviewDAO.class);
        groupTrainingService = new GroupTrainingServiceImpl(
                trainingTypeRepository,
                groupTrainingsRepository,
                groupTrainingsDAO,
                trainingTypeDAO,
                reviewDAO
        );
    }

    @Ignore
    @Test
    public void shouldUpdateGroupTraining_whenValidRequest()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
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
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainersIds, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

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
        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingBeforeUpdate.setId(id);

        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingAfterUpdate.setId(id);

        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        /*GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                rating, participantsResponses, reserveListResponses);*/

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(true);
        // TODO Fix Test
//        when(groupTrainingsDbRepositoryImpl.updateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(groupTrainingAfterUpdate);

        //then
        //TODO
        //assertThat(groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest)).isEqualTo(groupTrainingResponseAfterUpdate);
    }

    @Ignore
    @Test(expected = TrainingUpdateException.class)
    public void shouldNotUpdateGroupTraining_whenInvalidTrainingId()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
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
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainersIds, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

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
        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingBeforeUpdate.setId(id);
        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingAfterUpdate.setId(id);

        double rating = 0.0;
        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);
        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        /*GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                rating, participantsResponses, reserveListResponses);*/

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(false);
//        when(groupTrainingsDbRepositoryImpl.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(true);
        // TODO fix test
//        when(groupTrainingsDbRepositoryImpl.updateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(groupTrainingAfterUpdate);

        //then
        //TODO
        //groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest);
    }

    @Ignore
    @Test(expected = TrainingUpdateException.class)
    public void shouldNotUpdateGroupTraining_whenConflictWithOtherEvent()
            throws InvalidDateException, InvalidHourException {

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        List<String> trainersIds = new ArrayList<>();
        trainersIds.add(trainerId);
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
        GroupTrainingRequest groupTrainingUpdateRequest = new GroupTrainingRequest(trainingTypeId, trainersIds, date,
                startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate, participants,
                reserveList);

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
        GroupTrainings groupTrainingBeforeUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeBeforeUpdate, endTimeBeforeUpdate, hallNoBeforeUpdate, limitBeforeUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingBeforeUpdate.setId(id);
        GroupTrainings groupTrainingAfterUpdate = new GroupTrainings(trainingId, trainingType, trainersDocuments,
                date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                participantDocuments, reserveListDocuments);
        groupTrainingAfterUpdate.setId(id);

        double rating = 0.0;
        List<UserResponse> trainersResponse = new ArrayList<>();
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);
        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        /*GroupTrainingResponse groupTrainingResponseAfterUpdate = new GroupTrainingResponse(trainingId, trainingName,
                trainersResponse, date, startTimeAfterUpdate, endTimeAfterUpdate, hallNoAfterUpdate, limitAfterUpdate,
                rating, participantsResponses, reserveListResponses);*/

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.isAbilityToUpdateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(false);

        //TODO fix test
//        when(groupTrainingsDbRepositoryImpl.updateTraining(trainingId, groupTrainingUpdateRequest))
//                .thenReturn(groupTrainingAfterUpdate);

        //then
        //TODO
        //groupTrainingService.updateGroupTraining(trainingId, groupTrainingUpdateRequest);
    }
}
