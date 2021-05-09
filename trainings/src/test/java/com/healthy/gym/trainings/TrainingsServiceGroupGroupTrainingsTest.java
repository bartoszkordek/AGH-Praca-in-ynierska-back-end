package com.healthy.gym.trainings;

import com.healthy.gym.trainings.config.EmailConfig;
import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.mock.TrainingsServiceGroupGroupTrainingsImpl;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class TrainingsServiceGroupGroupTrainingsTest {

    private final String validTrainingId = "111111111111111111111111";
    private final String invalidTrainingId = "999999999999999999999999";
    private final String validClientId = "Client123";

    @TestConfiguration
    static class TrainingsServiceImplTestContextConfiguration {

        @Bean
        public GroupTrainingsService groupTrainingsService() {
            return new TrainingsServiceGroupGroupTrainingsImpl(null, null, null);
        }

        @Bean
        EmailConfig emailConfig() {
            return new EmailConfig();
        }

    }

    @Autowired
    GroupTrainingsService groupTrainingsService;

    @MockBean
    private GroupTrainingsDbRepository groupTrainingsDbRepository;

    @Before
    public void setUp() throws InvalidHourException {
        List<GroupTrainings> trainingsList = new ArrayList<>();
        List<String> participantsTraining1 = new ArrayList<>();
        List<String> reserveListTraining1 = new ArrayList<>();
        participantsTraining1.add("ASmith");
        GroupTrainings training1 = new GroupTrainings("Zumba", "99999", "01-06-2025", "18:00", "19:00",
                    2, 20, participantsTraining1, reserveListTraining1);
        training1.setId(validTrainingId);
        trainingsList.add(training1);

        when(groupTrainingsDbRepository.getGroupTrainings())
                .thenReturn(trainingsList);
        when(groupTrainingsDbRepository.getGroupTrainingById(validTrainingId))
                .thenReturn(training1);

        when(groupTrainingsDbRepository.isGroupTrainingExist(validTrainingId))
                .thenReturn(true);
        when(groupTrainingsDbRepository.isGroupTrainingExist(invalidTrainingId))
                .thenReturn(false);

        when(groupTrainingsDbRepository.getTrainingParticipants(validTrainingId))
                .thenReturn(participantsTraining1);
        when(groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(validTrainingId))
                .thenReturn(true);
        when(groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(invalidTrainingId))
                .thenReturn(false);

        when(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(validTrainingId, validClientId))
                .thenReturn(false);

        when(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(validTrainingId, validClientId))
                .thenReturn(false);

        }

    @Test
    public void shouldReturnFirstTrainingName_whenValidRequest() {
        assertThat(groupTrainingsService.getGroupTrainings().get(0).getTrainingName())
                    .isEqualTo("Zumba");
    }

    @Test
    public void shouldReturnTrainingNameByTrainingId_whenValidTrainingId() throws NotExistingGroupTrainingException {
        assertThat(groupTrainingsService.getGroupTrainingById(validTrainingId).getTrainingName())
                .isEqualTo("Zumba");
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldReturnExceptionTrainingId_whenInvalidTrainingId() throws NotExistingGroupTrainingException {
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotExistingGroupTrainingException.class)
                .when(groupTrainingsService)
                .getGroupTrainingById(invalidTrainingId);
        groupTrainingsService.getGroupTrainingById(invalidTrainingId);
    }


    @Test
    public void shouldReturnTrainingParticipants_whenValidTrainingId() throws NotExistingGroupTrainingException {
        List<String> participantsTraining1 = new ArrayList<>();
        participantsTraining1.add("ASmith");
        assertThat(groupTrainingsService.getTrainingParticipants(validTrainingId))
                .isEqualTo(participantsTraining1);
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldReturnEmptyTrainingParticipants_whenInvalidTrainingId() throws NotExistingGroupTrainingException {
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotExistingGroupTrainingException.class)
                .when(groupTrainingsService)
                .getTrainingParticipants(invalidTrainingId);
        groupTrainingsService.getTrainingParticipants(invalidTrainingId);
    }

    @Test
    public void shouldEnrollToGroupTraining_whenValidTrainingAndClientIdAndAvailability() throws TrainingEnrollmentException {
        groupTrainingsService.enrollToGroupTraining(validTrainingId, validClientId);

    }

    @Test(expected = TrainingEnrollmentException.class)
    public void shouldNotEnrollToGroupTraining_whenInvalidTraining() throws TrainingEnrollmentException {
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(TrainingEnrollmentException.class)
                .when(groupTrainingsService)
                .enrollToGroupTraining(invalidTrainingId,validClientId);
        groupTrainingsService.enrollToGroupTraining(invalidTrainingId,validClientId);
    }

    @Test
    public void shouldAddToReserveList_whenValidRequest() throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        //when
        when(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(validTrainingId, validClientId))
                .thenReturn(false);
        when(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(validTrainingId, validClientId))
                .thenReturn(false);
        //then
        groupTrainingsService.addToReserveList(validTrainingId,validClientId);
    }

    @Test(expected = NotExistingGroupTrainingException.class)
    public void shouldNotAddToReserveList_whenInvalidTraining() throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(NotExistingGroupTrainingException.class)
                .when(groupTrainingsService)
                .addToReserveList(invalidTrainingId,validClientId);
        groupTrainingsService.addToReserveList(invalidTrainingId,validClientId);
    }

    @Test
    public void shouldRemoveEnrollment_whenValidRequestAndClientExistsInParticipantsList() throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        //when
        when(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(validTrainingId, validClientId))
                .thenReturn(true);
        when(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(validTrainingId, validClientId))
                .thenReturn(false);
        //then
        groupTrainingsService.removeGroupTrainingEnrollment(validTrainingId,validClientId);
    }

    @Test(expected = TrainingEnrollmentException.class)
    public void shouldNotRemoveEnrollment_whenClientDoesntExistInParticipantsOrReserveList() throws NotExistingGroupTrainingException, TrainingEnrollmentException {
        //when
        when(groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(validTrainingId, validClientId))
                .thenReturn(false);
        when(groupTrainingsDbRepository.isClientAlreadyExistInReserveList(validTrainingId, validClientId))
                .thenReturn(false);
        //then
        GroupTrainingsService groupTrainingsService = mock(GroupTrainingsService.class);
        doThrow(TrainingEnrollmentException.class)
                .when(groupTrainingsService)
                .removeGroupTrainingEnrollment(invalidTrainingId,validClientId);
        groupTrainingsService.removeGroupTrainingEnrollment(invalidTrainingId,validClientId);
    }
}
