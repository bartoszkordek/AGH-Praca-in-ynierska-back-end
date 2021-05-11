package com.healthy.gym.trainings;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TrainingsServiceIndividualTrainingsTest {

    private final String validNotAcceptedTrainingId = "111111111111111111111111";
    private final String validAcceptedTrainingId = "222222222222222222222222";
    private final String declinedTrainingId = "444444444444444444444444";
    private final String invalidTrainingId = "999999999999999999999999";
    private final String validClientId = "Client123";
    private final String validTrainerId = "Trainer1";
    private final String validDate = "2025-01-01";
    private final String validStartTime = "18:00";
    private final String validEndTime = "19:00";
    private final int validHallNo = 1;
    private final int invalidHallNo = -1;
    private final String validRemarks = "(empty)";
    private final boolean isAccepted = false;
    private final boolean isDeclined = false;

    private IndividualTrainingsRequestModel validIndividualTrainingsRequestModel;
    private IndividualTrainings validIndividualTraining;

    private IndividualTrainingsAcceptModel individualTrainingsAcceptModel;
    private IndividualTrainingsAcceptModel invalidIndividualTrainingsAcceptModelWrongHallNo;
    private IndividualTrainings validAcceptedIndividualTraining;

    private IndividualTrainings acceptedIndividualTraining;

    private IndividualTrainings toDeclineIndividualTraining;
    private IndividualTrainings declinedIndividualTraining;


    public TrainingsServiceIndividualTrainingsTest(){

    }


    @TestConfiguration
    static class TrainingsServiceImplTestContextConfiguration {

        @Bean
        public IndividualTrainingsService individualTrainingsService() {
            return new TrainingsServiceIndividualTrainingsImpl(null);
        }
    }

    @Autowired
    IndividualTrainingsService individualTrainingsService;

    @MockBean
    private IndividualTrainingsDbRepository individualTrainingsDbRepository;

    @Before
    public void setUp() throws InvalidHourException {
        validIndividualTrainingsRequestModel = new IndividualTrainingsRequestModel(validTrainerId, validDate,
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

        when(individualTrainingsDbRepository.isIndividualTrainingExist(validNotAcceptedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(validAcceptedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(declinedTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.isIndividualTrainingExist(invalidTrainingId))
                .thenReturn(false);
        when(individualTrainingsDbRepository.getIndividualTrainingById(validNotAcceptedTrainingId))
                .thenReturn(validIndividualTraining);
        when(individualTrainingsDbRepository.createIndividualTrainingRequest(validIndividualTrainingsRequestModel, validClientId))
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
    public void shouldCreateIndividualTraining_whenValidRequestModelAndClientId() throws InvalidHourException {
        assertThat(individualTrainingsService.createIndividualTrainingRequest(validIndividualTrainingsRequestModel, validClientId))
                .isEqualTo(validIndividualTraining);
    }

    @Test
    public void shouldAcceptIndividualTraining_whenValidAcceptModelAndTrainingId() throws HallNoOutOfRangeException, NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException {
        assertThat(individualTrainingsService.acceptIndividualTraining(validNotAcceptedTrainingId, individualTrainingsAcceptModel))
                .isEqualTo(validAcceptedIndividualTraining);
    }

    @Test(expected = NotExistingIndividualTrainingException.class)
    public void shouldNotAcceptIndividualTraining_whenValidAcceptModelButTrainingIdDoesNotExist() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException {
        assertThat(individualTrainingsService.acceptIndividualTraining(invalidTrainingId, individualTrainingsAcceptModel));
    }

    @Test(expected = AlreadyAcceptedIndividualTrainingException.class)
    public void shouldNotAcceptIndividualTraining_whenValidModelTrainingExistButAccepted() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException {
        individualTrainingsService.acceptIndividualTraining(validAcceptedTrainingId, individualTrainingsAcceptModel);
    }

    @Test(expected = HallNoOutOfRangeException.class)
    public void shouldNotAcceptIndividualTraining_whenInvalidHallNo() throws NotExistingIndividualTrainingException, AlreadyAcceptedIndividualTrainingException, HallNoOutOfRangeException {
        individualTrainingsService.acceptIndividualTraining(validNotAcceptedTrainingId, invalidIndividualTrainingsAcceptModelWrongHallNo);
    }

    @Test
    public void shouldDeclineIndividualTraining_whenValidAndNotDeclinedTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException {
        assertThat(individualTrainingsService.declineIndividualTraining(validNotAcceptedTrainingId))
                .isEqualTo(validIndividualTraining);
    }

    @Test(expected = NotExistingIndividualTrainingException.class)
    public void shouldNotDeclineIndividualTraining_whenInvalidTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException {
        individualTrainingsService.declineIndividualTraining(invalidTrainingId);
    }

    @Test(expected = AlreadyDeclinedIndividualTrainingException.class)
    public void shouldNotDeclineIndividualTraining_whenValidButDeclinedTrainingId() throws NotExistingIndividualTrainingException, AlreadyDeclinedIndividualTrainingException {
        individualTrainingsService.declineIndividualTraining(declinedTrainingId);
    }

}
