package com.healthy.gym.trainings.service.training.type;

import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.ImageDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenCreateTrainingTypeTest {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeDAO trainingTypeDAO;
    private ImageDAO imageDAO;

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
        trainingTypeService = new TrainingTypeServiceImpl(trainingTypeDAO, imageDAO);

        multipartFile = new MockMultipartFile(
                "image",
                "hello.png",
                MediaType.IMAGE_PNG_VALUE,
                "data" .getBytes(StandardCharsets.UTF_8)
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
        private String imageId;
        private TrainingTypeDocument trainingTypeDocument;

        @BeforeEach
        void setUp() throws DuplicatedTrainingTypeException, IOException {
            imageId = UUID.randomUUID().toString();
            ImageDocument mockImageDocument = new ImageDocument(
                    imageId,
                    new Binary(multipartFile.getBytes()),
                    multipartFile.getContentType()
            );

            trainingTypeId = UUID.randomUUID().toString();
            TrainingTypeDocument mockTrainingTypeDocument = new TrainingTypeDocument(
                    trainingTypeId,
                    "Test name",
                    "Test description",
                    LocalTime.parse("13:24:40.001", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                    mockImageDocument
            );
            mockTrainingTypeDocument.setId(UUID.randomUUID().toString());

            when(trainingTypeDAO.existsByName("Test name")).thenReturn(false);
            when(trainingTypeDAO.save(any())).thenReturn(mockTrainingTypeDocument);
            when(imageDAO.save(any())).thenReturn(mockImageDocument);

            trainingTypeDocument = trainingTypeService.createTrainingType(request, multipartFile);
        }

        @Test
        void shouldHaveProperName() {
            assertThat(trainingTypeDocument.getName()).isEqualTo("Test name");
        }

        @Test
        void shouldHaveProperDescription() {
            assertThat(trainingTypeDocument.getDescription()).isEqualTo("Test description");
        }

        @Test
        void shouldHaveProperTrainingId() {
            assertThat(trainingTypeDocument.getTrainingTypeId()).isEqualTo(trainingTypeId);
        }

        @Test
        void shouldHaveProperImageDocument() throws IOException {
            assertThat(trainingTypeDocument.getImageDocument()).isEqualTo(
                    new ImageDocument(
                            imageId,
                            new Binary(multipartFile.getBytes()),
                            multipartFile.getContentType()
                    ));
        }

        @Test
        void shouldHaveProperDurationTime() {
            assertThat(trainingTypeDocument.getDuration())
                    .isEqualTo(LocalTime.parse("13:24:40.001", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
        }

        @Test
        void shouldSavedTrainingTypeDocumentHaveNonNullId() {
            assertThat(trainingTypeDocument.getId()).isNotNull();
        }
    }
}