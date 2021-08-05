package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
class TrainingTypeDAOIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    private TrainingTypeDocument trxDocument, pilatesDocument;
    private String trainingTypeId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

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

        pilatesDocument = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Pilates",
                "Test description 2",
                LocalTime.parse("00:30:00.000", DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                null
        );

        mongoTemplate.save(trxDocument);
        mongoTemplate.save(pilatesDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
    }

    @Test
    void shouldReturnAllTrainingTypeDocuments() {
        List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
        assertThat(foundTrainingTypes)
                .isNotNull()
                .hasSize(2)
                .extracting(TrainingTypeDocument::getName)
                .contains(
                        "TRX",
                        "Pilates"
                );
    }

    @Nested
    class WhenFindByName {
        @Test
        void shouldReturnProperTrainingTypeDocumentWhenTrainingTypeExists() {
            TrainingTypeDocument found = trainingTypeDAO.findByName("TRX");
            assertThat(found)
                    .isEqualTo(trxDocument)
                    .hasSameHashCodeAs(trxDocument);
        }

        @Test
        void shouldReturnNullWhenTrainingTypeNoExist() {
            TrainingTypeDocument found = trainingTypeDAO.findByName("Non existing training type");
            assertThat(found).isNull();
        }

        @Test
        void shouldReturnNullWhenNullAsArgument() {
            TrainingTypeDocument found = trainingTypeDAO.findByName(null);
            assertThat(found).isNull();
        }
    }

    @Nested
    class WhenFindByTrainingTypeId {
        @Test
        void shouldReturnProperTrainingTypeDocumentWhenTrainingTypeExists() {
            TrainingTypeDocument found = trainingTypeDAO.findByTrainingTypeId(trainingTypeId);
            assertThat(found)
                    .isEqualTo(trxDocument)
                    .hasSameHashCodeAs(trxDocument);
        }

        @Test
        void shouldReturnNullWhenTrainingTypeNoExist() {
            TrainingTypeDocument found = trainingTypeDAO.findByTrainingTypeId(UUID.randomUUID().toString());
            assertThat(found).isNull();
        }

        @Test
        void shouldReturnNullWhenNullAsArgument() {
            TrainingTypeDocument found = trainingTypeDAO.findByTrainingTypeId(null);
            assertThat(found).isNull();
        }
    }

    @Nested
    class WhenExistsByTrainingTypeId {
        @Test
        void shouldReturnTrue() {
            assertThat(trainingTypeDAO.existsByTrainingTypeId(trainingTypeId)).isTrue();
        }

        @Test
        void shouldReturnFalse() {
            assertThat(trainingTypeDAO.existsByTrainingTypeId(UUID.randomUUID().toString())).isFalse();
        }
    }

    @Nested
    class WhenExistsByName {
        @Test
        void shouldReturnTrue() {
            assertThat(trainingTypeDAO.existsByName("TRX")).isTrue();
        }

        @Test
        void shouldReturnFalse() {
            assertThat(trainingTypeDAO.existsByName("No existing type")).isFalse();
        }
    }

    @Nested
    class WhenDeleteByTrainingTypeId {
        @Test
        void shouldReturnDeleteWhenTrainingTypeExists() {
            trainingTypeDAO.deleteByTrainingTypeId(trainingTypeId);

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(1)
                    .extracting(TrainingTypeDocument::getName)
                    .contains("Pilates");
        }

        @Test
        void shouldDoNothingWhenTrainingTypeNoExist() {
            trainingTypeDAO.deleteByTrainingTypeId(UUID.randomUUID().toString());

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(TrainingTypeDocument::getName)
                    .contains(
                            "TRX",
                            "Pilates"
                    );
        }

        @Test
        void shouldReturnNullWhenNullAsArgument() {
            trainingTypeDAO.deleteByTrainingTypeId(null);

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(TrainingTypeDocument::getName)
                    .contains(
                            "TRX",
                            "Pilates"
                    );
        }
    }

    @Nested
    class WhenRemoveByName {
        @Test
        void shouldReturnDeleteWhenTrainingTypeExists() {
            trainingTypeDAO.removeByName("TRX");

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(1)
                    .extracting(TrainingTypeDocument::getName)
                    .contains("Pilates");
        }

        @Test
        void shouldDoNothingWhenTrainingTypeNoExist() {
            trainingTypeDAO.removeByName("No existing type");

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(TrainingTypeDocument::getName)
                    .contains(
                            "TRX",
                            "Pilates"
                    );
        }

        @Test
        void shouldReturnNullWhenNullAsArgument() {
            trainingTypeDAO.removeByName(null);

            List<TrainingTypeDocument> foundTrainingTypes = trainingTypeDAO.findAll();
            assertThat(foundTrainingTypes)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(TrainingTypeDocument::getName)
                    .contains(
                            "TRX",
                            "Pilates"
                    );
        }
    }

}