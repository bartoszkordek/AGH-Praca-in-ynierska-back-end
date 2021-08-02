package com.healthy.gym.trainings.service.groupTrainingService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.GroupTrainingsDbRepositoryImpl;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.EmailSendingException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;
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
        GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl = Mockito.mock(GroupTrainingsDbRepositoryImpl.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingsRepository groupTrainingsRepository = Mockito.mock(GroupTrainingsRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(
                emailConfig,
                groupTrainingsDbRepositoryImpl,
                trainingTypeRepository,
                groupTrainingsRepository
        );

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<UserDocument> participantDocuments = new ArrayList<>();
        List<UserDocument> reserveListDocuments = new ArrayList<>();

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1, 0, 0, 0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participantDocuments, reserveListDocuments);
        groupTraining.setId(id);

        double rating = 0.0;
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName, trainerId,
                date, startTime, endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(true);
        when(groupTrainingsDbRepositoryImpl.removeTraining(trainingId)).thenReturn(groupTraining);

        //then
        //TODO
        //assertThat(groupTrainingService.removeGroupTraining(trainingId)).isEqualTo(groupTrainingResponse);
    }

    @Ignore
    @Test(expected = TrainingRemovalException.class)
    public void shouldNotRemoveGroupTraining_whenInvalidTrainingId() throws InvalidDateException, InvalidHourException, TrainingRemovalException, EmailSendingException {
        //mocks
        EmailConfig emailConfig = Mockito.mock(EmailConfig.class);
        GroupTrainingsDbRepositoryImpl groupTrainingsDbRepositoryImpl = Mockito.mock(GroupTrainingsDbRepositoryImpl.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        GroupTrainingsRepository groupTrainingsRepository = Mockito.mock(GroupTrainingsRepository.class);
        GroupTrainingService groupTrainingService = new GroupTrainingServiceImpl(
                emailConfig,
                groupTrainingsDbRepositoryImpl,
                trainingTypeRepository,
                groupTrainingsRepository
        );

        //before
        String id = "507f1f77bcf86cd799439011";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainerId = "Test Trainer";
        String date = "2030-07-01";
        String startTime = "18:00";
        String endTime = "19:00";
        int hallNo = 1;
        int limit = 15;
        List<String> participants = new ArrayList<>();
        List<String> reserveList = new ArrayList<>();
        GroupTrainingRequest groupTrainingRequest = new GroupTrainingRequest(trainingTypeId, trainerId, date, startTime,
                endTime, hallNo, limit, participants, reserveList);

        String trainingName = "Test Training";
        String trainingDescription = "Sample description";
        LocalTime trainingDuration = LocalTime.of(1, 0, 0, 0);
        TrainingTypeDocument trainingType = new TrainingTypeDocument(trainingTypeId, trainingName, trainingDescription,
                trainingDuration, null);

        List<UserDocument> participantDocuments = new ArrayList<>();
        List<UserDocument> reserveListDocuments = new ArrayList<>();
        GroupTrainings groupTraining = new GroupTrainings(trainingId, trainingType, trainerId,
                date, startTime, endTime, hallNo, limit, participantDocuments, reserveListDocuments);
        groupTraining.setId(id);

        double rating = 0.0;
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponse groupTrainingResponse = new GroupTrainingResponse(trainingId, trainingName, trainerId,
                date, startTime, endTime, hallNo, limit, rating, participantsResponses, reserveListResponses);

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(false);
        when(groupTrainingsDbRepositoryImpl.removeTraining(trainingId)).thenReturn(groupTraining);

        //then
        //TODO
        //groupTrainingService.removeGroupTraining(trainingId);
    }
}
