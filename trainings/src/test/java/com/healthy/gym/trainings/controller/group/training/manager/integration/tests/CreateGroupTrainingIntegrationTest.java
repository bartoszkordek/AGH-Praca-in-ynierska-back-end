package com.healthy.gym.trainings.controller.group.training.manager.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.*;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
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
import org.springframework.test.context.TestPropertySource;
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

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
class CreateGroupTrainingIntegrationTest {

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
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent utilComponent;

    @LocalServerPort
    private Integer port;
    private String managerToken;
    private String requestContent;

    private String trainingTypeId;
    private String trainerId1;
    private String trainerId2;
    private String locationId;

    private UserDocument trainer1;
    private UserDocument trainer2;
    private LocationDocument location;

    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        managerToken = tokenFactory.getManagerToken(UUID.randomUUID().toString());

        trainingTypeId = UUID.randomUUID().toString();
        trainerId1 = UUID.randomUUID().toString();
        trainerId2 = UUID.randomUUID().toString();
        locationId = UUID.randomUUID().toString();

        objectMapper = new ObjectMapper();
        requestContent = objectMapper.writeValueAsString(getTestRequest());

        TrainingTypeDocument trainingType = new TrainingTypeDocument(
                trainingTypeId,
                "Test training name",
                null,
                null,
                null
        );
        mongoTemplate.save(trainingType);

        trainer1 = utilComponent.saveAndGetTestTrainer(trainerId1);
        trainer2 = utilComponent.saveAndGetTestTrainer(trainerId2);

        location = new LocationDocument(
                locationId,
                "TestLocationName"
        );
        mongoTemplate.save(location);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    private ManagerGroupTrainingRequest getTestRequest() {
        ManagerGroupTrainingRequest request = new ManagerGroupTrainingRequest();
        request.setTrainingTypeId(trainingTypeId);
        request.setTrainerIds(List.of(trainerId1, trainerId2));
        request.setStartDate("2020-10-10T16:00");
        request.setEndDate("2020-10-10T16:30");
        request.setLocationId(locationId);
        request.setLimit(20);
        return request;
    }

    private ResponseEntity<JsonNode> performAuthRequest() throws URISyntaxException {
        var request = getAuthRequest();
        URI uri = getUri();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.POST, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(requestContent, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", managerToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getUri() throws URISyntaxException {
        return new URI("http://localhost:" + port + "/group");
    }

    @Test
    void shouldCreateGroupTraining() throws URISyntaxException, JsonProcessingException {
        testDataBase(0);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("request.create.training.success");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode training = body.get("training");
        assert training != null;
        ObjectMapper objectMapper = new ObjectMapper();
        GroupTrainingDTO createdTraining = objectMapper
                .readValue(training.toString(), new TypeReference<>() {
                });

        assertThat(createdTraining.getGroupTrainingId()).isNotNull();
        assertThat(createdTraining.getTitle()).isEqualTo("Test training name");
        assertThat(createdTraining.getStartDate()).isEqualTo("2020-10-10T16:00");
        assertThat(createdTraining.getEndDate()).isEqualTo("2020-10-10T16:30");
        assertThat(createdTraining.isAllDay()).isFalse();
        assertThat(createdTraining.getLocation()).isEqualTo("TestLocationName");

        var trainers = createdTraining.getTrainers();

        assertThat(trainers.get(0).getUserId()).isEqualTo(trainer1.getUserId());
        assertThat(trainers.get(0).getName()).isEqualTo(trainer1.getName());
        assertThat(trainers.get(0).getSurname()).isEqualTo(trainer1.getSurname());

        assertThat(trainers.get(1).getUserId()).isEqualTo(trainer2.getUserId());
        assertThat(trainers.get(1).getName()).isEqualTo(trainer2.getName());
        assertThat(trainers.get(1).getSurname()).isEqualTo(trainer2.getSurname());

        var participants = createdTraining.getParticipants();
        assertThat(participants.getBasicList().size()).isZero();
        assertThat(participants.getReserveList().size()).isZero();

        testDataBase(1);
    }

    private void testDataBase(int expectedNumber) {
        var trainings = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(trainings.size()).isEqualTo(expectedNumber);
    }

    @Test
    void shouldThrowLocationNotFoundException() throws Exception {
        testDataBase(0);
        mongoTemplate.dropCollection(LocationDocument.class);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.location.not.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowLocationOccupiedException() throws Exception {
        utilComponent.saveAndGetTestIndividualTraining(
                "2020-10-10T15:45", "2020-10-10T16:16", location
        );

        testDataBase(0);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.create.group.training.location.occupied");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowPastDateException() throws Exception {
        var request = getTestRequest();
        request.setStartDate("2020-09-10T15:30");
        request.setEndDate("2020-09-10T16:30");
        requestContent = objectMapper.writeValueAsString(request);

        testDataBase(0);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

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
    void shouldThrowStartDateAfterEndDateException() throws Exception {
        var request = getTestRequest();
        request.setEndDate("2020-10-10T15:30");
        requestContent = objectMapper.writeValueAsString(request);

        testDataBase(0);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

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
        utilComponent.saveAndGetTestIndividualTraining(
                "2020-10-10T15:45", "2020-10-10T16:16", List.of(trainer1)
        );
        testDataBase(0);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.create.group.training.trainer.occupied");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

    @Test
    void shouldThrowTrainerNotFoundException() throws Exception {
        testDataBase(0);
        mongoTemplate.dropCollection(UserDocument.class);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

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
    void shouldThrowTrainingTypeNotFoundException() throws Exception {
        testDataBase(0);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);

        ResponseEntity<JsonNode> responseEntity = performAuthRequest();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.create.group.training.trainingType.not.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(0);
    }

}
