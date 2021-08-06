package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.repository.GroupTrainingsDAO;
import com.healthy.gym.trainings.data.repository.GroupTrainingsRepository;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponseOld;
import com.healthy.gym.trainings.model.response.UserResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GetGroupTrainingServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

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

    @Test
    public void shouldReturnAllGroupTrainings_whenValidRequest()
            throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {
        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        LocalDateTime trainingStartDate = LocalDateTime.of(2021,7,1,18,0);
        LocalDateTime trainingEndDate = LocalDateTime.of(2021,7,1,19,0);
        String locationName = "Hall 1";
        int limit = 15;

        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(trainingId, trainingName, trainersResponse,
                trainingStartDate, trainingEndDate, locationName, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponseOld> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
//        when(groupTrainingsDbRepositoryImpl.getGroupTrainings(startDate, endDate)).thenReturn(groupTrainings);

        //then
//        assertThat(groupTrainingService.getGroupTrainings(startDate, endDate)).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnAllGroupTrainingsPublicView_whenValidRequest()
            throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        LocalDateTime dayBeforeStartDate = LocalDateTime.of(1999,12,31,23,59);
        LocalDateTime dayAfterEndDate = LocalDateTime.of(2031,1,1,0,0);
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        LocalDateTime trainingStartDate = LocalDateTime.of(2021,7,1,18,0);
        LocalDateTime trainingEndDate = LocalDateTime.of(2021,7,1,19,0);
        String locationName = "Hall 1";
        int limit = 15;
        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        GroupTrainingPublicResponse groupTrainingPublicResponse = new GroupTrainingPublicResponse(trainingId,
                trainingName, trainersResponse, trainingStartDate, trainingEndDate, locationName, limit, rating);

        List<GroupTrainingPublicResponse> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTrainingPublicResponse);

        //when
        /*when(groupTrainingsDAO.findByStartDateAfterAndEndDateBefore(dayBeforeStartDate, dayAfterEndDate))
                .thenReturn(groupTrainings);*/
        //when(groupTrainingsDbRepositoryImpl.getPublicGroupTrainings(startDate, endDate)).thenReturn(groupTrainings);

        //then
        //assertThat(groupTrainingService.getPublicGroupTrainings(startDate, endDate)).isEqualTo(groupTrainings);
    }

    @Test
    public void shouldReturnGroupTrainingByTrainingId_whenValidRequest()
            throws InvalidHourException, NotExistingGroupTrainingException, InvalidDateException {
        //before
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        LocalDateTime trainingStartDate = LocalDateTime.of(2021,7,1,18,0);
        LocalDateTime trainingEndDate = LocalDateTime.of(2021,7,1,19,0);
        String locationName = "Hall 1";
        int limit = 15;
        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(
                trainingId,
                trainingName,
                trainersResponse,
                trainingStartDate,
                trainingEndDate,
                locationName,
                limit,
                rating,
                participantsResponses,
                reserveListResponses
        );

        List<GroupTrainingResponseOld> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.getGroupTrainingById(trainingId)).thenReturn(groupTraining);

        //then
//        assertThat(groupTrainingService.getGroupTrainingById(trainingId)).isEqualTo(groupTraining);
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotReturnGroupTrainingByTrainingId_whenInvalidTrainingId()
            throws InvalidHourException, NotExistingGroupTrainingException, InvalidDateException {
        //before
        String invalidTrainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";

        //when
        when(groupTrainingsDAO.existsById(invalidTrainingId)).thenReturn(false);

        //then
        //groupTrainingService.getGroupTrainingById(invalidTrainingId);
    }

    @Test
    public void shouldGetTrainingParticipants_whenValidTrainingId() throws NotExistingGroupTrainingException {
        //before
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String participant1UserId = "222ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String participant1Name = "John";
        String participant1Surname = "Smith";
        String participant2UserId = "222ed953-e37f-435a-bd1e-9fb2a327c4d4";
        String participant2Name = "Max";
        String participant2Surname = "Adams";
        List<UserResponse> participantsResponses = new ArrayList<>();
        UserResponse participants1Response = new UserResponse(participant1UserId, participant1Name,
                participant1Surname);
        UserResponse participants2Response = new UserResponse(participant2UserId, participant2Name,
                participant2Surname);
        participantsResponses.add(participants1Response);
        participantsResponses.add(participants2Response);

        //when
        when(groupTrainingsRepository.existsByTrainingId(trainingId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.getTrainingParticipants(trainingId))
//                .thenReturn(participantsResponses);

        //then
//        assertThat(groupTrainingService.getTrainingParticipants(trainingId));
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotGetTrainingParticipants_whenInvalidTrainingId() throws NotExistingGroupTrainingException {
        //before
        String invalidTrainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";

        //when
        when(groupTrainingsRepository.existsByTrainingId(invalidTrainingId)).thenReturn(false);

        //then
        //groupTrainingService.getTrainingParticipants(invalidTrainingId);
    }

    @Test
    public void shouldReturnGroupTrainingsByTrainingTypeId_whenValidRequest()
            throws InvalidDateException, InvalidHourException, ParseException,
            StartDateAfterEndDateException, NotExistingGroupTrainingException, TrainingTypeNotFoundException {

        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        LocalDateTime trainingStartDate = LocalDateTime.of(2021,7,1,18,0);
        LocalDateTime trainingEndDate = LocalDateTime.of(2021,7,1,19,0);
        String locationName = "Hall 1";
        int limit = 15;
        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(trainingId, trainingName, trainersResponse,
                trainingStartDate, trainingEndDate, locationName, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponseOld> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate))
//                .thenReturn(groupTrainings);

        //then
//        assertThat(groupTrainingService.getGroupTrainingsByType(trainingTypeId, startDate, endDate))
//                .isEqualTo(groupTrainings);
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnGroupTrainings_whenInvalidTrainingTypeId()
            throws InvalidDateException, InvalidHourException, StartDateAfterEndDateException,
            ParseException, NotExistingGroupTrainingException, TrainingTypeNotFoundException {
        //before
        String startDate = "2000-01-01";
        String endDate = "2030-12-31";
        String trainingId = "122ed953-e37f-435a-bd1e-9fb2a327c4d3";
        String trainingTypeId = "222ed952-es7f-435a-bd1e-9fb2a327c4dk";
        String trainingName = "Test Training";
        String trainerId = "Test Trainer";
        LocalDateTime trainingStartDate = LocalDateTime.of(2021,7,1,18,0);
        LocalDateTime trainingEndDate = LocalDateTime.of(2021,7,1,19,0);
        String locationName = "Hall 1";
        int limit = 15;
        double rating = 0.0;

        List<UserResponse> trainersResponse = new ArrayList<>();
        String trainer1Name = "John";
        String trainer1Surname = "Smith";
        String trainer1UserId = "100ed952-es7f-435a-bd1e-9fb2a327c4dk";
        UserResponse trainer1Response = new UserResponse(trainer1UserId, trainer1Name, trainer1Surname);
        trainersResponse.add(trainer1Response);

        List<UserResponse> participantsResponses = new ArrayList<>();
        List<UserResponse> reserveListResponses = new ArrayList<>();
        GroupTrainingResponseOld groupTraining = new GroupTrainingResponseOld(trainingId, trainingName, trainersResponse,
                trainingStartDate, trainingEndDate, locationName, limit, rating, participantsResponses, reserveListResponses);

        List<GroupTrainingResponseOld> groupTrainings = new ArrayList<>();
        groupTrainings.add(groupTraining);

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(false);
        when(groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
//        when(groupTrainingsDbRepositoryImpl.getGroupTrainingsByTrainingTypeId(trainingTypeId, startDate, endDate))
//                .thenReturn(groupTrainings);

        //then
        groupTrainingService.getGroupTrainingsByType(trainingTypeId, startDate, endDate);
    }

}
