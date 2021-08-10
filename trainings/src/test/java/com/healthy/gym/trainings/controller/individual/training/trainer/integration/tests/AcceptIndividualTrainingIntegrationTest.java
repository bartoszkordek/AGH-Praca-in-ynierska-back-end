package com.healthy.gym.trainings.controller.individual.training.trainer.integration.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class AcceptIndividualTrainingIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @Autowired
    private TestRestTemplate restTemplate;

    private String trainingId;
    private String trainerId;
    private String locationId;
    private URI uri;
    private String trainerToken;
    private IndividualTrainingDocument document;
    private LocationDocument locationDocument;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        document = testUtil.saveAndGetTestIndividualTraining(
                "2020-10-09T10:10",
                "2020-10-09T11:00",
                false
        );
        trainingId = document.getIndividualTrainingId();
        trainerId = document.getTrainers().get(0).getUserId();
        trainerToken = tokenFactory.getTrainerToken(trainerId);
        locationDocument = testUtil.saveAndGetTestLocation();
        locationId = locationDocument.getLocationId();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Test
    void shouldAcceptIndividualTrainingRequest() throws Exception {
        testDataBase(false);

        uri = new URI("http://localhost:" + port + "/individual/trainer/"
                + trainerId + "/training/" + trainingId + "?locationId=" + locationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("enrollment.individual.accepted");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode training = body.get("training");
        assert training != null;
        ObjectMapper objectMapper = new ObjectMapper();
        IndividualTrainingDTO individualTrainingDTO = objectMapper
                .readValue(training.toString(), new TypeReference<>() {
                });

        assertThat(individualTrainingDTO.getIndividualTrainingId()).isEqualTo(trainingId);

        var trainer = individualTrainingDTO.getTrainers().get(0);
        assertThat(trainer.getUserId()).isEqualTo(trainerId);
        assertThat(trainer.getName()).isEqualTo(document.getTrainers().get(0).getName());
        assertThat(trainer.getSurname()).isEqualTo(document.getTrainers().get(0).getSurname());

        assertThat(individualTrainingDTO.isAccepted()).isTrue();
        assertThat(individualTrainingDTO.getLocation()).isEqualTo(locationDocument.getName());

        testDataBase(true);
    }

    private void testDataBase(boolean expected) {
        var trainings = mongoTemplate.findAll(IndividualTrainingDocument.class);
        var training = trainings.get(0);
        assertThat(training.isAccepted()).isEqualTo(expected);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.PUT, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(null, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", trainerToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void shouldThrowAlreadyAcceptedIndividualTrainingException() throws Exception {
        document.setAccepted(true);
        document = mongoTemplate.save(document);

        testDataBase(true);

        uri = new URI("http://localhost:" + port + "/individual/trainer/"
                + trainerId + "/training/" + trainingId + "?locationId=" + locationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.already.accepted.individual.training");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(true);
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenAnotherTrainerTriesToAcceptRequest() throws Exception {
        document = testUtil.saveAndGetTestIndividualTraining(
                "2020-10-09T10:10",
                "2020-10-09T11:00",
                false
        );
        trainingId = document.getIndividualTrainingId();
        trainerId = UUID.randomUUID().toString();
        testUtil.saveAndGetTestTrainer(trainerId);

        testDataBase(false);

        uri = new URI("http://localhost:" + port + "/individual/trainer/"
                + trainerId + "/training/" + trainingId + "?locationId=" + locationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.access.denied");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }

    @Test
    void shouldThrowPastDateExceptionWhenTrainerTriesToAcceptPastRequest() throws Exception {
        document = testUtil.saveAndGetTestIndividualTraining(
                "2020-09-09T10:10",
                "2020-09-09T11:00",
                false
        );
        trainingId = document.getIndividualTrainingId();
        trainerId = document.getTrainers().get(0).getUserId();
        trainerToken = tokenFactory.getTrainerToken(trainerId);

        testDataBase(false);

        uri = new URI("http://localhost:" + port + "/individual/trainer/"
                + trainerId + "/training/" + trainingId + "?locationId=" + locationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.past.date");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }
}

