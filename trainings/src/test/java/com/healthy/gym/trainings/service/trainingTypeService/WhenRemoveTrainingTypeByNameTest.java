package com.healthy.gym.trainings.service.trainingTypeService;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenRemoveTrainingTypeByNameTest {

    @Autowired
    private TrainingTypeService trainingTypeService;

    @MockBean
    private TrainingTypeDAO trainingTypeDAO;

    private TrainingTypeDocument trxDocument;
    private String trainingTypeId;

    @BeforeEach
    void setUp() {
        trainingTypeId = UUID.randomUUID().toString();
        trxDocument = new TrainingTypeDocument(
                trainingTypeId,
                "TRX",
                "Test description",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );
    }

    @Test
    void shouldThrowExceptionWhenNoTrainingTypeFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(null);
        assertThatThrownBy(() -> trainingTypeService.removeTrainingTypeById(trainingTypeId))
                .isInstanceOf(TrainingTypeNotFoundException.class);
    }

    @Test
    void shouldReturnTrainingTypeById() throws TrainingTypeNotFoundException {
        when(trainingTypeDAO.findByTrainingTypeId(trainingTypeId)).thenReturn(trxDocument);
        assertThat(trainingTypeService.removeTrainingTypeById(trainingTypeId)).isEqualTo(trxDocument);
    }
}
