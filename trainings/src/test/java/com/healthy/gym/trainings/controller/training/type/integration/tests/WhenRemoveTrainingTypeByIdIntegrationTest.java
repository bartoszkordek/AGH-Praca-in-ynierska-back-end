package com.healthy.gym.trainings.controller.training.type.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenRemoveTrainingTypeByIdIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;

    private String trainingTypeId;
    private Resource imageResource;
    private String adminToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws IOException {
        trainingTypeId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRemoveTrainingTypeDocument(TestCountry country) throws URISyntaxException, IOException {
        List<TrainingTypeDocument> trainingTypeDocumentList = mongoTemplate.findAll(TrainingTypeDocument.class);
        assertThat(trainingTypeDocumentList.size()).isEqualTo(1);

        List<ImageDocument> imageDocumentList = mongoTemplate.findAll(ImageDocument.class);
        assertThat(imageDocumentList.size()).isEqualTo(1);

        Locale testedLocale = convertEnumToLocale(country);
        URI uri = new URI("http://localhost:" + port + "/trainingType/" + trainingTypeId);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                getRequestEntity(testedLocale),
                JsonNode.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("training.type.removed");
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);

        assertThat(responseEntity.getBody().get("errors")).isNull();
        assertThat(responseEntity.getBody().get("trainingTypeId").textValue()).isEqualTo(trainingTypeId);
        assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Test name");
        assertThat(responseEntity.getBody().get("description").textValue()).isEqualTo("Test description");
        assertThat(responseEntity.getBody().get("duration").textValue()).isEqualTo("02:30:00.000");
        assertThat(responseEntity.getBody().get("image").get("data").textValue())
                .isEqualTo(getExpectedImageBase64());
        assertThat(responseEntity.getBody().get("image").get("format").textValue()).isEqualTo("image/jpeg");

        trainingTypeDocumentList = mongoTemplate.findAll(TrainingTypeDocument.class);
        assertThat(trainingTypeDocumentList.size()).isZero();
        imageDocumentList = mongoTemplate.findAll(ImageDocument.class);
        assertThat(imageDocumentList.size()).isZero();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhileRemovingNonExistingTrainingType(TestCountry country) throws URISyntaxException {
        Locale testedLocale = convertEnumToLocale(country);
        URI uri = new URI("http://localhost:" + port + "/trainingType/" + UUID.randomUUID());
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                getRequestEntity(testedLocale),
                JsonNode.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("exception.not.found.training.type");
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
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
}
