package com.healthy.gym.trainings;

import com.healthy.gym.trainings.config.EmailConfig;
import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.mock.TrainingsServiceIndividualTrainingsImpl;
import com.healthy.gym.trainings.model.IndividualTrainingsAcceptModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TrainingsServiceIndividualTrainingsTest {

    private final String validNotAcceptedTrainingId = "111111111111111111111111";
    private final String validAcceptedTrainingId = "222222222222222222222222";
    private final String declinedTrainingId = "444444444444444444444444";
    private final String retroTrainingId = "555555555555555555555555";
    private final String invalidTrainingId = "999999999999999999999999";
    private final String validClientId = "Client123";
    private final String invalidClientId = "InvalidClient123";
    private final String validTrainerId = "Trainer1";
    private final String validDate = "2025-01-01";
    private final String retroDate = "2000-01-01";
    private final String validStartTime = "18:00";
    private final String validEndTime = "19:00";
    private final int validHallNo = 1;
    private final int invalidHallNo = -1;
    private final String validRemarks = "(empty)";
    private final boolean isAccepted = false;
    private final boolean isDeclined = false;

    private IndividualTrainingsRequestModel validIndividualTrainingsRequestModel;
    private IndividualTrainingsRequestModel terminatedIndividualTrainingsRequestModelRetroDate;
    private IndividualTrainings validIndividualTraining;

    private IndividualTrainingsAcceptModel individualTrainingsAcceptModel;
    private IndividualTrainingsAcceptModel invalidIndividualTrainingsAcceptModelWrongHallNo;
    private IndividualTrainings validAcceptedIndividualTraining;

    private IndividualTrainings acceptedIndividualTraining;
    private IndividualTrainings declinedIndividualTraining;

    private IndividualTrainings retroIndividualTraining;


    public TrainingsServiceIndividualTrainingsTest(){

    }


    @TestConfiguration
    static class TrainingsServiceImplTestContextConfiguration {

        @Bean
        public IndividualTrainingsService individualTrainingsService() {
            return new TrainingsServiceIndividualTrainingsImpl(null);
        }

        @Bean
        EmailConfig emailConfig() {
            return new EmailConfig();
        }
    }

    @Autowired
    IndividualTrainingsService individualTrainingsService;

    @MockBean
    private IndividualTrainingsDbRepository individualTrainingsDbRepository;

    @Before
    public void setUp() throws InvalidHourException, InvalidDateException {
        validIndividualTrainingsRequestModel = new IndividualTrainingsRequestModel(validTrainerId, validDate,
                validStartTime, validEndTime, validRemarks);
        terminatedIndividualTrainingsRequestModelRetroDate = new IndividualTrainingsRequestModel(validTrainerId, retroDate,
                validStartTime, validEndTime, validRemarks);
        validIndividualTraining = new  IndividualTrainings(validClientId, validTrainerId,
                validDate, validStartTime, validEndTime, validHallNo, validRemarks, isAccepted, isDeclined);
        validIndividualTraining.setId(validNotAcceptedTrainingId);

        individualTrainingsAcceptModel = new IndividualTrainingsAcceptModel(validHallNo);
        validAcceptedIndividualTraining = new  IndividualTrainings(validClientId, validTrainerId,
                validDate, validStartTime, validEndTime, validHallNo, validRemarks, true, isDeclined);
        validAcceptedIndividualTraining.setId(validAcceptedTrainingId);

        invalidIndividualTrainingsAcceptModelWrongHallNo = new IndividualTrainingsAcceptModel(invalidHallNo);

        acceptedIndividualTraining = new  IndividualTrainings(validClientId, validTrainerId,
                validDate, validStartTime, validEndTime, validHallNo, validRemarks, true, isDeclined);
        acceptedIndividualTraining.setId(validNotAcceptedTrainingId);

        declinedIndividualTraining = new  IndividualTrainings(validClientId, validTrainerId,
                validDate, validStartTime, validEndTime, validHallNo, validRemarks, isAccepted, true);
        declinedIndividualTraining.setId(declinedTrainingId);

        retroIndividualTraining = new IndividualTrainings(validClientId, validTrainerId,
                retroDate, validStartTime, validEndTime, validHallNo, validRemarks, isAccepted, isDeclined);
        retroIndividualTraining.setId(retroTrainingId);

        when(individualTrainingsDbRepository.isIndividualTrainingExist(validNotAcceptedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(validAcceptedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(declinedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(invalidTrainingId))
                .thenReturn(false);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(retroTrainingId))
                .thenReturn(true);

        when(individualTrainingsDbRepository.getIndividualTrainingById(validNotAcceptedTrainingId))
                .thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.getIndividualTrainingById(retroTrainingId))
                .thenReturn(retroIndividualTraining);

        when(individualTrainingsDbRepository.createIndividualTrainingRequest(validIndividualTrainingsRequestModel, validClientId))
                .thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.createIndividualTrainingRequest(terminatedIndividualTrainingsRequestModelRetroDate, validClientId))
                .thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndAccepted(validNotAcceptedTrainingId))
                .thenReturn(false);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndAccepted(validAcceptedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.acceptIndividualTrainingRequest(validNotAcceptedTrainingId, individualTrainingsAcceptModel))
                .thenReturn(validAcceptedIndividualTraining);
        when(individualTrainingsDbRepository.declineIndividualTrainingRequest(validNotAcceptedTrainingId))
                .thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndDeclined(validNotAcceptedTrainingId))
                .thenReturn(false);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndDeclined(declinedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(validNotAcceptedTrainingId, validClientId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(validNotAcceptedTrainingId, invalidClientId))
                .thenReturn(false);
        when(individualTrainingsDbRepository.isIndividualTrainingExistAndRequestedByClient(retroTrainingId, validClientId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.cancelIndividualTrainingRequest(validNotAcceptedTrainingId)).
                thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.cancelIndividualTrainingRequest(retroTrainingId)).
                thenReturn(retroIndividualTraining);

    }

    @Test
    public void shouldReturnIndividualTraining_whenValidTrainingId() throws NotExistingIndividualTrainingException {
        assertThat(individualTrainingsService.getIndividualTrainingById(validNotAcceptedTrainingId))
                .isEqualTo(validIndividualTraining);
    }

    @Test(expected = NotExistingIndividualTrainingException.class)
    public void shouldReturnExceptionTrainingId_whenInvalidTrainingId() throws NotExistingIndividualTrainingException {
        individualTrainingsService.getIndividualTrainingById(invalidTrainingId);
    }

    @Test
    public void shouldCreateIndividualTraining_whenValidRequestModelAndClientId() throws InvalidHourException, ParseException, RetroIndividualTrainingException {
        assertThat(individualTrainingsService.createIndividualTrainingRequest(validIndividualTrainingsRequestModel, validClientId))
                .isEqualTo(validIndividualTraining);
    }

    @Test (expected = RetroIndividualTrainingException.class)
    public void shouldNotCreateIndividualTraining_whenTerminatedRequest() throws InvalidHourException, ParseException, RetroIndividualTrainingException {
        individualTrainingsService.createIndividualTrainingRequest(terminatedIndividualTrainingsRequestModelRetroDate, validClientId);
    }

    @Test
    public void shouldAcceptIndividualTraining_whenValidAcceptModelAndTrainingId() throws HallNoOutOfRangeException, NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, ParseException, RetroIndividualTrainingException, EmailSendingException {
        assertThat(individualTrainingsService.acceptIndividualTraining(validNotAcceptedTrainingId, individualTrainingsAcceptModel))
                .isEqualTo(validAcceptedIndividualTraining);
    }

    @Test (expected = RetroIndividualTrainingException.class)
    public void shouldNotAcceptIndividualTraining_whenTerminatedRequest() throws HallNoOutOfRangeException, NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, ParseException, RetroIndividualTrainingException, EmailSendingException {
        individualTrainingsService.acceptIndividualTraining(retroTrainingId, individualTrainingsAcceptModel);
    }

    @Test(expected = NotExistingIndividualTrainingException.class)
    public void shouldNotAcceptIndividualTraining_whenValidAcceptModelButTrainingIdDoesNotExist() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException, EmailSendingException {
        assertThat(individualTrainingsService.acceptIndividualTraining(invalidTrainingId, individualTrainingsAcceptModel));
    }

    @Test(expected = AlreadyAcceptedIndividualTrainingException.class)
    public void shouldNotAcceptIndividualTraining_whenValidModelTrainingExistButAccepted() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException, EmailSendingException {
        individualTrainingsService.acceptIndividualTraining(validAcceptedTrainingId, individualTrainingsAcceptModel);
    }

    @Test(expected = HallNoOutOfRangeException.class)
    public void shouldNotAcceptIndividualTraining_whenInvalidHallNo() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException, ParseException, RetroIndividualTrainingException, EmailSendingException {
        individualTrainingsService.acceptIndividualTraining(validNotAcceptedTrainingId, invalidIndividualTrainingsAcceptModelWrongHallNo);
    }

    @Test
    public void shouldDeclineIndividualTraining_whenValidAndNotDeclinedTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException, EmailSendingException {
        assertThat(individualTrainingsService.declineIndividualTraining(validNotAcceptedTrainingId))
                .isEqualTo(validIndividualTraining);
    }

    @Test(expected = NotExistingIndividualTrainingException.class)
    public void shouldNotDeclineIndividualTraining_whenInvalidTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException, EmailSendingException {
        individualTrainingsService.declineIndividualTraining(invalidTrainingId);
    }

    @Test(expected = AlreadyDeclinedIndividualTrainingException.class)
    public void shouldNotDeclineIndividualTraining_whenValidButDeclinedTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException, EmailSendingException {
        individualTrainingsService.declineIndividualTraining(declinedTrainingId);
    }

    @Test
    public void shouldCancelIndividualTraining_whenValidRequest() throws NotAuthorizedClientException, RetroIndividualTrainingException, NotExistingIndividualTrainingException, ParseException {
        assertThat(individualTrainingsService.cancelIndividualTrainingRequest(validNotAcceptedTrainingId, validClientId))
                .isEqualTo(validIndividualTraining);
    }

    @Test (expected = NotExistingIndividualTrainingException.class)
    public void shouldNotCancelIndividualTraining_whenInvalidTrainingId() throws NotAuthorizedClientException, RetroIndividualTrainingException, NotExistingIndividualTrainingException, ParseException {
        individualTrainingsService.cancelIndividualTrainingRequest(invalidTrainingId, validClientId);
    }

    @Test (expected = NotAuthorizedClientException.class)
    public void shouldNotCancelIndividualTraining_whenNotRequestedByClient() throws NotAuthorizedClientException, RetroIndividualTrainingException, NotExistingIndividualTrainingException, ParseException {
        individualTrainingsService.cancelIndividualTrainingRequest(validNotAcceptedTrainingId, invalidClientId);
    }

    @Test (expected = RetroIndividualTrainingException.class)
    public void shouldNotCancelIndividualTraining_whenRetroDate() throws NotAuthorizedClientException, RetroIndividualTrainingException, NotExistingIndividualTrainingException, ParseException {
        individualTrainingsService.cancelIndividualTrainingRequest(retroTrainingId, validClientId);
    }

}
