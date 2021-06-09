package com.healthy.gym.trainings;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.mock.TrainingTypeServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class TrainingTypeServiceTest {


    private final String validTrainingTypeId = "111111111111111111111111";
    private final String invalidTrainingTypeId = "999999999999999999999999";
    @Autowired
    TrainingTypeServiceImpl trainingTypeService;
    private TrainingTypeDocument validTrainingType;
    @MockBean
    private TrainingTypeDAO trainingTypeRepository;

    public TrainingTypeServiceTest() {

    }

    @Before
    public void setUp() {


        validTrainingType = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Valid Training Name",
                "Sample Description",
                null,
                null
        );
        validTrainingType.setId(validTrainingTypeId);
        when(trainingTypeRepository.existsTrainingTypeById(validTrainingTypeId))
                .thenReturn(true);
        when(trainingTypeRepository.findByTrainingTypeId(validTrainingTypeId))
                .thenReturn(validTrainingType);
    }

    @Test
    public void shouldReturnTrainingTypeById_whenValidRequest() throws NotExistingTrainingType {
        assertThat(trainingTypeService.getTrainingTypeById(validTrainingTypeId))
                .isEqualTo(validTrainingType);
    }

    @Test(expected = NotExistingTrainingType.class)
    public void shouldNotReturnTrainingTypeById_whenTrainingIdNotExists() throws NotExistingTrainingType {
        trainingTypeService.getTrainingTypeById(invalidTrainingTypeId);
    }

    @TestConfiguration
    static class TrainingTypesServiceImplTestContextConfiguration {

        @Bean
        public TrainingTypeServiceImpl trainingTypeService() {
            return new TrainingTypeServiceImpl(null);
        }

    }


}
