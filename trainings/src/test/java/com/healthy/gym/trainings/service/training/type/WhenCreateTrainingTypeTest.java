package com.healthy.gym.trainings.service.training.type;

import com.healthy.gym.trainings.component.ImageUrlCreator;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.ImageDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.dto.TrainingTypeDTO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.service.TrainingTypeService;
import com.healthy.gym.trainings.service.TrainingTypeServiceImpl;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

class WhenCreateTrainingTypeTest {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeDAO trainingTypeDAO;
    private ImageDAO imageDAO;
    private ImageUrlCreator imageUrlCreator;

    private TrainingTypeRequest request;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        request = new TrainingTypeRequest();
        request.setName("Test name");
        request.setDescription("Test description");
        request.setDuration("13:24:40.001");

        trainingTypeDAO = mock(TrainingTypeDAO.class);
        imageDAO = mock(ImageDAO.class);
        imageUrlCreator = mock(ImageUrlCreator.class);
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeDAO, imageDAO, imageUrlCreator);

        multipartFile = new MockMultipartFile(
                "image",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainingTypeAlreadyExists() {
        when(trainingTypeDAO.existsByName("Test name")).thenReturn(true);

        assertThatThrownBy(() -> trainingTypeService.createTrainingType(request, null))
                .isInstanceOf(DuplicatedTrainingTypeException.class);
    }

    @Nested
    class ShouldSaveTrainingType {
        private String trainingTypeId;
        private TrainingTypeDTO trainingTypeDTO;

        @BeforeEach
        void setUp() throws DuplicatedTrainingTypeException, IOException {
            String imageId = UUID.randomUUID().toString();
            ImageDocument mockImageDocument = new ImageDocument(
                    imageId,
                    new Binary(multipartFile.getBytes()),
                    multipartFile.getContentType()
            );

            trainingTypeId = UUID.randomUUID().toString();


            when(trainingTypeDAO.existsByName("Test name")).thenReturn(false);
            when(imageDAO.save(any())).thenReturn(mockImageDocument);
            when(imageUrlCreator.createImageUrl(anyString()))
                    .thenReturn("http://localhost:8020/trainings/trainingType/image/imageID?version=11");

            TrainingTypeDocument mockTrainingTypeDocument = new TrainingTypeDocument(
                    trainingTypeId,
                    "Test name",
                    "Test description",
                    LocalTime.parse("13:24:40.001", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                    mockImageDocument
            );
            mockTrainingTypeDocument.setId(UUID.randomUUID().toString());
            mockTrainingTypeDocument
                    .setImageUrl("http://localhost:8020/trainings/trainingType/image/imageID?version=11");

            when(trainingTypeDAO.save(any())).thenReturn(mockTrainingTypeDocument);

            trainingTypeDTO = trainingTypeService.createTrainingType(request, multipartFile);
        }

        @Test
        void shouldHaveProperName() {
            assertThat(trainingTypeDTO.getName()).isEqualTo("Test name");
        }

        @Test
        void shouldHaveProperDescription() {
            assertThat(trainingTypeDTO.getDescription()).isEqualTo("Test description");
        }

        @Test
        void shouldHaveProperTrainingId() {
            assertThat(trainingTypeDTO.getTrainingTypeId()).isEqualTo(trainingTypeId);
        }

        @Test
        void shouldHaveProperImageDocument() {
            assertThat(trainingTypeDTO.getImageUrl())
                    .isEqualTo("http://localhost:8020/trainings/trainingType/image/imageID?version=11");
        }

        @Test
        void shouldHaveProperDurationTime() {
            assertThat(trainingTypeDTO.getDuration()).isEqualTo("13:24:40.001");
        }

        @Test
        void shouldSavedTrainingTypeDocumentHaveNonNullId() {
            assertThat(trainingTypeDTO.getTrainingTypeId()).isNotNull();
        }
    }
}