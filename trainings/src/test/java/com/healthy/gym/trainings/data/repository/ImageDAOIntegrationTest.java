package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.ImageDocument;
import org.apache.http.entity.ContentType;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles(value = "test")
@Tags({@Tag("repository"), @Tag("integration")})
class ImageDAOIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ImageDAO imageDAO;
    private ImageDocument image1, image2, image3;
    private String imageId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        imageId = UUID.randomUUID().toString();
        image1 = new ImageDocument(
                imageId,
                getImageFromResourceFolderAsByte("testImages/shiba_inu_smile_1.jpg"),
                ContentType.IMAGE_JPEG.getMimeType()
        );

        image2 = new ImageDocument(
                UUID.randomUUID().toString(),
                getImageFromResourceFolderAsByte("testImages/shiba_inu_smile_2.jpg"),
                ContentType.IMAGE_JPEG.getMimeType()
        );

        image3 = new ImageDocument(
                UUID.randomUUID().toString(),
                getImageFromResourceFolderAsByte("testImages/shiba_inu_smile_3.jpg"),
                ContentType.IMAGE_JPEG.getMimeType()
        );

        mongoTemplate.save(image1);
        mongoTemplate.save(image2);
        mongoTemplate.save(image3);
    }

    private Binary getImageFromResourceFolderAsByte(String path) throws IOException {
        File shibaInuSmileImg = ResourceUtils.getFile("classpath:" + path);
        FileInputStream fileInputStream = new FileInputStream(shibaInuSmileImg);
        byte[] bytes = fileInputStream.readAllBytes();
        return new Binary(bytes);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(ImageDocument.class);
    }

    @Test
    void shouldReturnAllImagesFromDatabase() {
        List<ImageDocument> found = imageDAO.findAll();
        assertThat(found).isNotNull()
                .hasSize(3)
                .extracting(ImageDocument::getImageId)
                .contains(
                        image1.getImageId(),
                        image2.getImageId(),
                        image3.getImageId()
                );

    }

    @Nested
    class WhenFindByImageId {
        @Test
        void shouldReturnProperImageDocumentWhenImageExists() {
            ImageDocument foundImage = imageDAO.findByImageId(imageId);
            assertThat(foundImage).isEqualTo(image1);
        }

        @Test
        void shouldReturnNullWhenImageDoesNotExist() {
            String nonExistingImageId = UUID.randomUUID().toString();
            ImageDocument foundImage = imageDAO.findByImageId(nonExistingImageId);
            assertThat(foundImage).isNull();
        }

        @Test
        void shouldReturnNullWhenNullProvided() {
            ImageDocument foundImage = imageDAO.findByImageId(null);
            assertThat(foundImage).isNull();
        }
    }
}