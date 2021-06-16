package com.healthy.gym.trainings.controller.trainingTypeController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenGetTrainingTypeByIdIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    private String trainingTypeId;
    private TrainingTypeRequest trainingTypeRequest;
    private Resource imageResource;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws IOException {
        trainingTypeId = UUID.randomUUID().toString();

        TrainingTypeRequest trainingTypeRequest = new TrainingTypeRequest();
        trainingTypeRequest.setName("Test name");
        trainingTypeRequest.setDescription("Test description");
        trainingTypeRequest.setDuration("02:30:00.000");

        imageResource = new ClassPathResource("testImages/shiba_inu_smile_1.jpg");

        ImageDocument imageDocument = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument = mongoTemplate.save(imageDocument);

        TrainingTypeDocument typeDocument = new TrainingTypeDocument(
                trainingTypeId,
                "Test name",
                "Test description",
                LocalTime.of(2, 30),
                savedImageDocument
        );

        mongoTemplate.save(typeDocument);

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

    private String getExpectedImageBase64() throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] imageBytes = inputStream.readAllBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(imageBytes);
    }

    @Nested
    class shouldAcceptRequestAndShouldResponseWithProperTrainingType {
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
        void shouldReturnMessageIsNull(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNoErrors(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("errors")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperTrainingTypeId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("trainingTypeId").textValue()).isEqualTo(trainingTypeId);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperName(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Test name");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("description").textValue()).isEqualTo("Test description");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDuration(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("duration").textValue()).isEqualTo("02:30:00.000");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageData(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("image").get("data").textValue())
                    .isEqualTo(getExpectedImageBase64());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageFormat(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("image").get("format").textValue()).isEqualTo("image/jpeg");
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/trainingType/" + trainingTypeId);
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class shouldAcceptRequestAndShouldResponseWithNotFoundTrainingType{

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
            String expectedMessage = messages.get("exception.not.found.training.type");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/trainingType/" + UUID.randomUUID());
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }
}
