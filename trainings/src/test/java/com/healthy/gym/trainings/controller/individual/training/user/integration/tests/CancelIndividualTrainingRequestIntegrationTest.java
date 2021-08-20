package com.healthy.gym.trainings.controller.individual.training.user.integration.tests;

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
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
class CancelIndividualTrainingRequestIntegrationTest {

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
    private String userId;
    private String locationId;
    private URI uri;
    private String userToken;
    private IndividualTrainingDocument document;
    private UserDocument user;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        user = testUtil.saveAndGetTestUser(userId);
        document = testUtil.saveAndGetTestIndividualTraining(
                "2020-10-10T10:00",
                "2020-10-10T11:00",
                user
        );
        trainingId = document.getIndividualTrainingId();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    private URI getUri(String userId, String trainingId) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/individual/user/" + userId + "/training/" + trainingId);
    }

    @Test
    void shouldCancelIndividualTrainingRequest() throws Exception {
        testDataBase(false);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);
        System.out.println(responseEntity);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("enrollment.remove");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode training = body.get("training");
        assert training != null;
        ObjectMapper objectMapper = new ObjectMapper();
        IndividualTrainingDTO individualTrainingDTO = objectMapper
                .readValue(training.toString(), new TypeReference<>() {
                });

        assertThat(individualTrainingDTO.getIndividualTrainingId()).isEqualTo(trainingId);
        assertThat(individualTrainingDTO.isCancelled()).isTrue();

        var participants = individualTrainingDTO.getParticipants();
        var basicList = participants.getBasicList();

        List<String> listOfUserIds = basicList
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .collect(Collectors.toList());

        assertThat(listOfUserIds.contains(userId)).isTrue();
        assertThat(individualTrainingDTO.isCancelled()).isTrue();

        testDataBase(true);
    }

    private void testDataBase(boolean expected) {
        var trainings = mongoTemplate.findAll(IndividualTrainingDocument.class);
        var training = trainings.get(0);
        assertThat(training.isCancelled()).isEqualTo(expected);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.DELETE, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(null, headers);
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

    @Test
    void shouldThrowAlreadyCancelledIndividualTrainingException() throws Exception {
        document.setCancelled(true);
        document = mongoTemplate.save(document);

        testDataBase(true);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.already.cancelled.individual.training");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(true);
    }

    @Test
    void shouldThrowIndividualTrainingHasBeenRejectedException() throws Exception {
        document.setRejected(true);
        document = mongoTemplate.save(document);

        testDataBase(false);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.already.rejected.individual.training");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }

    @Test
    void shouldThrowPastDateException() throws Exception {
        document.setStartDateTime(LocalDateTime.parse("2020-09-10T10:10"));
        document.setEndDateTime(LocalDateTime.parse("2020-09-10T11:10"));
        document = mongoTemplate.save(document);

        testDataBase(false);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.past.date.enrollment.remove");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }

    @Test
    void shouldThrowNotExistingIndividualTrainingException() throws Exception {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.existing.individual.training");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldThrowUserNotFoundException() throws Exception {
        mongoTemplate.remove(user);

        testDataBase(false);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.found.user.id");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }

    @Test
    void shouldThrowUserIsNotParticipantException() throws Exception {
        document.setBasicList(List.of());
        document = mongoTemplate.save(document);

        testDataBase(false);

        uri = getUri(userId, trainingId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.user.is.not.participant");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDataBase(false);
    }
}
