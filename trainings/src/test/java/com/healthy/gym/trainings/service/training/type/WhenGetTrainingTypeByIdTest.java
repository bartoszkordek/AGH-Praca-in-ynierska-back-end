package com.healthy.gym.trainings.service.training.type;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.TrainingTypeService;
import com.healthy.gym.trainings.service.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class WhenGetTrainingTypeByIdTest {

    private TrainingTypeService trainingTypeService;
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

        trainingTypeDAO = mock(TrainingTypeDAO.class);
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeDAO, null, null);
    }

    @Test
    void shouldThrowExceptionWhenNoTrainingTypeFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(null);
        assertThatThrownBy(() -> trainingTypeService.getTrainingTypeById(trainingTypeId))
                .isInstanceOf(TrainingTypeNotFoundException.class);
    }

    @Test
    void shouldReturnTrainingTypeById() throws TrainingTypeNotFoundException {
        when(trainingTypeDAO.findByTrainingTypeId(trainingTypeId)).thenReturn(trxDocument);
        assertThat(trainingTypeService.getTrainingTypeById(trainingTypeId)).isEqualTo(trxDocument);
    }
}
