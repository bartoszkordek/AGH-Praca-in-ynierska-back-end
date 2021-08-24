package com.healthy.gym.trainings.service.training.type;

import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.ImageDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.service.TrainingTypeService;
import com.healthy.gym.trainings.service.TrainingTypeServiceImpl;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class WhenUpdateTrainingTypeByIdTest {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeDAO trainingTypeDAO;
    private ImageDAO imageDAO;

    private TrainingTypeRequest request;
    private MockMultipartFile multipartFile;
    private String trainingTypeId;
    private TrainingTypeDocument trainingTypeDocument;
    private ImageDocument imageToUpdate;
    private LocalTime localTime;

    @BeforeEach
    void setUp() throws IOException {
        trainingTypeId = UUID.randomUUID().toString();

        request = new TrainingTypeRequest();
        request.setName("Test name2");
        request.setDescription("Test description");
        request.setDuration("00:30:00.001");

        multipartFile = new MockMultipartFile(
                "image",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data".getBytes(StandardCharsets.UTF_8)
        );

        imageToUpdate = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(multipartFile.getBytes()),
                multipartFile.getContentType()
        );
        imageToUpdate.setId(UUID.randomUUID().toString());

        localTime = LocalTime.parse("00:30:00.001", DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        trainingTypeDocument = new TrainingTypeDocument(
                trainingTypeId,
                "Test name",
                "Test description",
                localTime,
                imageToUpdate
        );

        trainingTypeDAO = mock(TrainingTypeDAO.class);
        imageDAO = mock(ImageDAO.class);
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeDAO, imageDAO, null);
    }

    @Test
    void shouldThrowExceptionWhenNoTrainingTypeFound() {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(null);
        assertThatThrownBy(() -> trainingTypeService.updateTrainingTypeById(trainingTypeId, request, multipartFile))
                .isInstanceOf(TrainingTypeNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenTrainingTypeAlreadyExists() {
        when(trainingTypeDAO.existsByName(anyString())).thenReturn(true);
        when(trainingTypeDAO.findByTrainingTypeId(anyString()))
                .thenReturn(new TrainingTypeDocument(
                        trainingTypeId,
                        "Test name",
                        "Test description",
                        localTime,
                        new ImageDocument()
                ));

        assertThatThrownBy(() -> trainingTypeService.updateTrainingTypeById(trainingTypeId, request, multipartFile))
                .isInstanceOf(DuplicatedTrainingTypeException.class);
    }

    @Test
    void shouldUpdateTrainingType() throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException {
        when(trainingTypeDAO.findByTrainingTypeId(anyString())).thenReturn(trainingTypeDocument);
        when(trainingTypeDAO.existsByName(anyString())).thenReturn(false);

        when(imageDAO.save(any())).thenReturn(imageToUpdate);
        when(trainingTypeDAO.save(any())).thenReturn(
                new TrainingTypeDocument(
                        trainingTypeId,
                        "Test name2",
                        "Test description",
                        localTime,
                        imageToUpdate
                )
        );

        assertThat(trainingTypeService.updateTrainingTypeById(trainingTypeId, request, multipartFile))
                .isEqualTo(new TrainingTypeDocument(
                        trainingTypeId,
                        "Test name2",
                        "Test description",
                        localTime,
                        imageToUpdate
                ));
    }
}
