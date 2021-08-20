package com.healthy.gym.trainings.controller.individual.training.user.integration.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
class GetMyAllIndividualTrainingsIntegrationTest {

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
    private URI uri;
    private String userToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {


        List<String[]> data = List.of(
                new String[]{"2021-01-01T10:10", "2021-01-01T11:00"},
                new String[]{"2021-01-02T10:10", "2021-01-02T11:00"},
                new String[]{"2021-01-03T10:10", "2021-01-03T11:00"},
                new String[]{"2021-01-04T10:10", "2021-01-04T11:00"},
                new String[]{"2021-01-05T10:10", "2021-01-05T11:00"},

                new String[]{"2021-01-06T10:10", "2021-01-06T11:00"},
                new String[]{"2021-01-06T20:10", "2021-01-06T21:00"},
                new String[]{"2021-01-07T10:10", "2021-01-07T11:00"},
                new String[]{"2021-01-08T10:10", "2021-01-08T11:00"},
                new String[]{"2021-01-09T10:10", "2021-01-09T11:00"},

                new String[]{"2021-01-10T10:10", "2021-01-10T11:00"},
                new String[]{"2021-01-10T12:10", "2021-01-10T12:00"}
        );

        data.forEach(strings -> testUtil.saveAndGetTestIndividualTraining(strings[0], strings[1]));

        var user = testUtil.saveAndGetTestUser();
        userId = user.getUserId();
        userToken = tokenFactory.getUserToken(userId);

        testUtil.saveAndGetTestIndividualTraining("2021-01-03T10:10", "2021-01-03T11:00", user);
        testUtil.saveAndGetTestIndividualTraining("2021-01-02T10:10", "2021-01-02T13:00", user);
        testUtil.saveAndGetTestIndividualTraining("2021-01-09T10:10", "2021-01-09T13:00", user);
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
        return restTemplate.exchange(uri, HttpMethod.GET, request, JsonNode.class);
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

    private URI getUri(String userId, String startDate, String endDate) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/individual/user/" + userId
                + "?startDate=" + startDate
                + "&endDate=" + endDate
        );
    }

    @ParameterizedTest
    @CsvSource({
            "2021-01-01,2021-01-12,3",
            "2021-01-01,2021-01-06,2",
            "2021-01-03,2021-01-06,1",
            "2021-01-05,2021-01-10,1",
            "2021-01-01,2021-01-04,2",
    })
    void shouldGetIndividualTrainingById(String startDate, String endDate, int expectedNumber) throws Exception {
        uri = getUri(userId, startDate, endDate);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.get("message")).isNull();

        ObjectMapper objectMapper = new ObjectMapper();
        List<IndividualTrainingDTO> individualTrainingDTOs = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });

        assertThat(individualTrainingDTOs.size()).isEqualTo(expectedNumber);
        var count1 = individualTrainingDTOs.get(0)
                .getParticipants()
                .getBasicList()
                .stream()
                .map(BasicUserInfoDTO::getUserId)
                .filter(s -> s.equals(userId)).count();
        assertThat(count1).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-02-01,2021-02-09",
            "2021-01-04,2021-01-05",
    })
    void shouldThrowNoIndividualTrainingFoundException(String startDate, String endDate) throws Exception {
        uri = getUri(userId, startDate, endDate);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.no.individual.training.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-02-10,2021-02-09",
            "2021-01-02,2021-01-01",
    })
    void shouldThrowStartDateAfterEndDateException(String startDate, String endDate) throws Exception {
        uri = getUri(userId, startDate, endDate);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.start.date.after.end.date");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
