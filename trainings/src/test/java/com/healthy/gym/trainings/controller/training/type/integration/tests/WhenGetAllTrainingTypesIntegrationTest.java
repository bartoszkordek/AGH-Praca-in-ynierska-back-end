package com.healthy.gym.trainings.controller.training.type.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
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
class WhenGetAllTrainingTypesIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private Integer port;
    private Resource imageResource1;
    private Resource imageResource2;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws IOException {
        imageResource1 = new ClassPathResource("testImages/shiba_inu_smile_1.jpg");

        ImageDocument imageDocument1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource1)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument1 = mongoTemplate.save(imageDocument1);

        TrainingTypeDocument typeDocument1 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name",
                "Test description",
                LocalTime.of(1, 30),
                savedImageDocument1
        );

        mongoTemplate.save(typeDocument1);

        imageResource2 = new ClassPathResource("testImages/shiba_inu_smile_2.jpg");

        ImageDocument imageDocument2 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource2)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument2 = mongoTemplate.save(imageDocument2);

        TrainingTypeDocument typeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name2",
                "Test description2",
                LocalTime.of(2, 30),
                savedImageDocument2
        );

        mongoTemplate.save(typeDocument2);
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
        void shouldReturnProperName(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("name").textValue()).isEqualTo("Test name");
            assertThat(responseEntity.getBody().get(1).get("name").textValue()).isEqualTo("Test name2");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).
                    get("description").textValue()).isEqualTo("Test description");
            assertThat(responseEntity.getBody().get(1).
                    get("description").textValue()).isEqualTo("Test description2");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDuration(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("duration").textValue()).isEqualTo("01:30:00.000");
            assertThat(responseEntity.getBody().get(1).get("duration").textValue()).isEqualTo("02:30:00.000");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageData(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("image").get("data").textValue())
                    .isEqualTo(getExpectedImageBase64(imageResource1));
            assertThat(responseEntity.getBody().get(1).get("image").get("data").textValue())
                    .isEqualTo(getExpectedImageBase64(imageResource2));
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageFormat(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get(0).get("image").get("format").textValue()).isEqualTo("image/jpeg");
            assertThat(responseEntity.getBody().get(1).get("image").get("format").textValue()).isEqualTo("image/jpeg");
        }


        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/trainingType");
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class shouldAcceptRequestAndShouldResponseWithNotFoundTrainingType {

        @BeforeEach
        void setUp() {
            mongoTemplate.dropCollection(ImageDocument.class);
            mongoTemplate.dropCollection(TrainingTypeDocument.class);
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
            String expectedMessage = messages.get("exception.not.found.training.type.all");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/trainingType");
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }
}
