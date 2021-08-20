package com.healthy.gym.trainings.controller.individual.training.user.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
class CreateIndividualTrainingRequestIntegrationTest {

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
    private TestDocumentUtilComponent testUtil;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @Autowired
    private TestRestTemplate restTemplate;

    private String userId;
    private String trainerId;
    private URI uri;
    private String userToken;
    private String requestBody;
    private UserDocument trainer;
    private UserDocument user;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        user = testUtil.saveAndGetTestUser(userId);

        trainerId = UUID.randomUUID().toString();
        trainer = testUtil.saveAndGetTestTrainer(trainerId);

        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-10T11:10");
    }

    private String getRequestBody(String startDateTime, String endDateTime) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        IndividualTrainingRequest trainingRequest = new IndividualTrainingRequest();
        trainingRequest.setStartDateTime(startDateTime);
        trainingRequest.setEndDateTime(endDateTime);
        trainingRequest.setTrainerId(trainerId);
        trainingRequest.setRemarks("Test remarks");

        return objectMapper.writeValueAsString(trainingRequest);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.POST, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(requestBody, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", userToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/individual/user/" + userId);
    }

    @Test
    void shouldGetIndividualTrainingById() throws Exception {
        testDataBase(0);
        uri = getUri(userId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("enrollment.success.individual");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode training = body.get("training");
        ObjectMapper objectMapper = new ObjectMapper();
        IndividualTrainingDTO individualTrainingDTO = objectMapper
                .readValue(training.toString(), new TypeReference<>() {
                });

        assertThat(individualTrainingDTO.getStartDate()).isEqualTo("2020-10-10T10:10");
        assertThat(individualTrainingDTO.getEndDate()).isEqualTo("2020-10-10T11:10");
        assertThat(individualTrainingDTO.getRemarks()).isEqualTo("Test remarks");

        List<String> trainers = individualTrainingDTO.getTrainers()
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .collect(Collectors.toList());

        assertThat(trainers.contains(trainerId)).isTrue();

        List<String> basicList = individualTrainingDTO
                .getParticipants()
                .getBasicList()
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .collect(Collectors.toList());

        assertThat(basicList.contains(userId)).isTrue();
        assertThat(individualTrainingDTO.isAccepted()).isFalse();
        assertThat(individualTrainingDTO.isCancelled()).isFalse();
        assertThat(individualTrainingDTO.isRejected()).isFalse();

        testDataBase(1);
    }

    private void testDataBase(int expectedNumber) {
        var trainings = mongoTemplate.findAll(IndividualTrainingDocument.class);
        assertThat(trainings.size()).isEqualTo(expectedNumber);
    }

    @Test
    void shouldThrowPastDateException() throws Exception {
        testDataBase(0);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-09-10T10:10", "2020-09-10T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.past.date");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowInvalidTrainerSpecifiedException() throws Exception {
        mongoTemplate.dropCollection(UserDocument.class);
        user = testUtil.saveAndGetTestUser(userId);
        trainer = testUtil.saveAndGetTestUser(trainerId);

        testDataBase(0);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-10T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.invalid.trainer.specified");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowStartDateAfterEndDateException() throws Exception {
        testDataBase(0);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-09T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.start.date.after.end.date");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowTrainerOccupiedException() throws Exception {
        testUtil.saveAndGetTestIndividualTraining(
                "2020-10-10T09:10",
                "2020-10-10T10:30",
                List.of(trainer)
        );

        testDataBase(1);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-10T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.create.group.training.trainer.occupied");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(1);
    }

    @Test
    void shouldThrowTrainerNotFoundException() throws Exception {
        mongoTemplate.remove(trainer);

        testDataBase(0);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-10T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.create.group.training.trainer.not.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowUserNotFoundException() throws Exception {
        mongoTemplate.remove(user);

        testDataBase(0);

        uri = getUri(userId);
        requestBody = getRequestBody("2020-10-10T10:10", "2020-10-10T11:10");

        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.found.user.id");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }
}
