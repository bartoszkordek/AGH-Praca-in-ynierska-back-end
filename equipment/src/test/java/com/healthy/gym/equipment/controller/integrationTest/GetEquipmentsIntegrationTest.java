package com.healthy.gym.equipment.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.equipment.component.TokenManager;
import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.ImageDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
import com.healthy.gym.equipment.configuration.TestCountry;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.*;

import static com.healthy.gym.equipment.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.equipment.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetEquipmentsIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        Resource imageResource1 = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        ImageDocument imageDocument1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource1)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument1 = mongoTemplate.save(imageDocument1);

        String trainingTypeId1 = UUID.randomUUID().toString();
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                trainingTypeId1,
                "Test name",
                "Test description",
                LocalTime.of(1, 30),
                savedImageDocument1,
                "http://localhost:8020/trainings/trainingType/" + trainingTypeId1 + "?version=test1"
        );
        mongoTemplate.save(trainingTypeDocument1);

        Resource imageResourceEquipment1 = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        ImageDocument imageDocumentEquipment1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResourceEquipment1)),
                MediaType.IMAGE_JPEG_VALUE
        );
        ImageDocument savedImageDocumentEquipment1 = mongoTemplate.save(imageDocumentEquipment1);

        String equipmentId1 = UUID.randomUUID().toString();
        EquipmentDocument equipmentDocument1 = new EquipmentDocument(
                equipmentId1,
                "Test equipment title 1",
                List.of(savedImageDocumentEquipment1),
                List.of("http://localhost:8020/equipment/" + equipmentId1 + "?version=test1"),
                "Test synopsis 1",
                List.of(trainingTypeDocument1)
        );

        mongoTemplate.save(equipmentDocument1);

        Resource imageResource2 = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        ImageDocument imageDocument2 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource2)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument2 = mongoTemplate.save(imageDocument2);

        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name2",
                "Test description2",
                LocalTime.of(2, 30),
                savedImageDocument2,
                "http://localhost:8020/trainings/trainingType/" + trainingTypeId1 + "?version=test1"
        );

        mongoTemplate.save(trainingTypeDocument2);

        String equipmentId2 = UUID.randomUUID().toString();
        EquipmentDocument equipmentDocument2 = new EquipmentDocument(
                equipmentId2,
                "Test equipment title 2",
                List.of(savedImageDocumentEquipment1),
                List.of("http://localhost:8020/equipment/" + equipmentId2 + "?version=test1"),
                "Test synopsis 2",
                List.of(trainingTypeDocument2)
        );

        mongoTemplate.save(equipmentDocument2);
    }

    private byte[] getImageBytes(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        return inputStream.readAllBytes();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(ImageDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(EquipmentDocument.class);
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        return headers;
    }

    private HttpEntity<Object> getRequestEntity(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        return new HttpEntity<>(null, headers);
    }

    private String getExpectedImageBase64(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] imageBytes = inputStream.readAllBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(imageBytes);
    }

    @Nested
    class ShouldAcceptRequestAndShouldResponseWithProperTrainingTypes {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusOK(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperMediaType(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnArray(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().isArray()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNoErrors(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("errors")).isNull();
            assertThat(responseEntity.getBody().get(1).get("errors")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotNullEquipmentIds(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("equipmentId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("equipmentId")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).
                    get("description")).isNotNull();
            assertThat(responseEntity.getBody().get(0).
                    get("description").get("synopsis").textValue()).isEqualTo("Test synopsis 1");
            assertThat(responseEntity.getBody().get(0).
                    get("description").get("trainings")).isNotNull();
            assertThat(responseEntity.getBody().get(0).
                    get("description").get("trainings").get(0)).isNotNull();
            assertThat(responseEntity.getBody().get(0).
                    get("description").get("trainings").get(0).get("trainingId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).
                    get("description").get("trainings").get(0).get("title").textValue()).isEqualTo("Test name");
            assertThat(responseEntity.getBody().get(1).
                    get("description")).isNotNull();
            assertThat(responseEntity.getBody().get(1).
                    get("description").get("synopsis").textValue()).isEqualTo("Test synopsis 2");
            assertThat(responseEntity.getBody().get(1).
                    get("description").get("trainings")).isNotNull();
            assertThat(responseEntity.getBody().get(1).
                    get("description").get("trainings").get(0)).isNotNull();
            assertThat(responseEntity.getBody().get(1).
                    get("description").get("trainings").get(0).get("trainingId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).
                    get("description").get("trainings").get(0).get("title").textValue()).isEqualTo("Test name2");

        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageUrl(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("images")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("images").get(0)).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("images")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("images").get(0)).isNotNull();
        }


        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port );
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class shouldAcceptRequestAndShouldResponseWithNotFoundEquipment {

        @BeforeEach
        void setUp() {
            mongoTemplate.dropCollection(ImageDocument.class);
            mongoTemplate.dropCollection(TrainingTypeDocument.class);
            mongoTemplate.dropCollection(EquipmentDocument.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusNotFound(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnExpectedMessage(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            String expectedMessage = messages.get("exception.not.found.equipment.all");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port );
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }
}
