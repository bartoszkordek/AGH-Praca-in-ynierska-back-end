package com.healthy.gym.trainings;

import com.healthy.gym.trainings.db.IndividualTrainingsDbRepository;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.mock.TrainingsServiceIndividualTrainingsImpl;
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

    private final String validTrainingId = "111111111111111111111111";
    private final String invalidTrainingId = "999999999999999999999999";
    private final String validClientId = "Client123";
    private final String validTrainerId = "Trainer1";
    private final String validDate = "2025-01-01";
    private final String validStartTime = "18:00";
    private final String validEndTime = "19:00";
    private final int validHallNo = 1;
    private final String validRemarks = "(empty)";
    private final boolean isAccepted = false;
    private final boolean isDeclined = false;

    private IndividualTrainings validIndividualTraining;


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
        validIndividualTraining = new  IndividualTrainings(validClientId, validTrainerId,
                validDate, validStartTime, validEndTime, validHallNo, validRemarks, isAccepted, isDeclined);
        validIndividualTraining.setId(validTrainingId);

        when(individualTrainingsDbRepository.isIndividualTrainingExist(validTrainingId))
                .thenReturn(true);
        when(individualTrainingsDbRepository.getIndividualTrainingById(validTrainingId))
                .thenReturn(validIndividualTraining);
    }

    @Test
    public void shouldReturnIndividualTraining_whenValidTrainingId() throws NotExistingIndividualTrainingException {
        assertThat(individualTrainingsService.getIndividualTrainingById(validTrainingId).getId())
                .isEqualTo(validIndividualTraining.getId());
    }



}
