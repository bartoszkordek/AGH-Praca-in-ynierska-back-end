package com.healthy.gym.trainings.controller.group.training.universal.integration.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
class UniversalGroupTrainingControllerIntegrationTest {

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

    private String trainingTypeId;
    private URI uri;
    private String userToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        TrainingTypeDocument trainingType = testUtil.saveAndGetTestTrainingType();
        trainingTypeId = trainingType.getTrainingTypeId();
        userToken = tokenFactory.getUserToken();

        testUtil.createTestGroupTraining("2020-10-01T11:30", "2020-10-01T12:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-01T12:30", "2020-10-01T13:00");
        testUtil.createTestGroupTraining("2020-10-02T12:30", "2020-10-02T13:00");
        testUtil.createTestGroupTraining("2020-10-03T13:30", "2020-10-03T14:00");
        testUtil.createTestGroupTraining("2020-10-04T14:30", "2020-10-04T15:00");
        testUtil.createTestGroupTraining("2020-10-05T15:30", "2020-10-05T16:00");
        testUtil.createTestGroupTraining("2020-10-06T16:30", "2020-10-06T17:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-06T19:30", "2020-10-06T20:00");
        testUtil.createTestGroupTraining("2020-10-07T17:30", "2020-10-07T18:00");
        testUtil.createTestGroupTraining("2020-10-08T18:30", "2020-10-08T19:00");
        testUtil.createTestGroupTraining(trainingType, "2020-10-08T20:30", "2020-10-08T22:00");
        testUtil.createTestGroupTraining("2020-10-09T19:30", "2020-10-09T20:00");
        testUtil.createTestGroupTraining("2020-10-10T20:30", "2020-10-10T21:00");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,13",
            "2020-10-01,2020-10-05,6",
            "2020-10-09,2020-10-10,2",
            "2020-10-04,2020-10-07,5",
            "2020-10-03,2020-10-06,5",
            "2020-10-04,2020-10-08,7",
            "2020-10-01,2020-10-05,6",
    })
    void shouldGetGroupTrainingsWithoutParticipantsWithinDates(
            String startDate, String endDate, int numberOfTrainingsWithinDates
    ) throws Exception {
        testDatabaseState();

        uri = new URI("http://localhost:" + port + "/group/public?startDate=" + startDate + "&endDate=" + endDate);
        ResponseEntity<JsonNode> responseEntity = performRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(numberOfTrainingsWithinDates);

        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupTrainingWithoutParticipantsDTO> groupTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });

        assertThat(groupTrainingDTOList.size()).isEqualTo(numberOfTrainingsWithinDates);
        testDatabaseState();
    }

    private void testDatabaseState() {
        List<GroupTrainingDocument> groupTrainingList = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainingList.size()).isEqualTo(13);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.GET, request, JsonNode.class);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri) {
        var request = getRequest();
        return performRequest(uri, request);
    }

    private HttpEntity<Object> getRequest() {
        var headers = getHeaders();
        return new HttpEntity<>(null, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,13",
            "2020-10-01,2020-10-05,6",
            "2020-10-09,2020-10-10,2",
            "2020-10-04,2020-10-07,5",
            "2020-10-03,2020-10-06,5",
            "2020-10-04,2020-10-08,7",
            "2020-10-01,2020-10-05,6",
    })
    void shouldGetGroupTrainingsWithParticipantsWithinDates(
            String startDate, String endDate, int numberOfTrainingsWithinDates
    ) throws Exception {
        testDatabaseState();

        uri = new URI("http://localhost:" + port + "/group?startDate=" + startDate + "&endDate=" + endDate);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(numberOfTrainingsWithinDates);

        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupTrainingDTO> groupTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });
        assertThat(groupTrainingDTOList.size()).isEqualTo(numberOfTrainingsWithinDates);

        testDatabaseState();
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
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

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,3",
            "2020-10-01,2020-10-05,1",
            "2020-10-09,2020-10-10,0",
            "2020-10-04,2020-10-07,1",
            "2020-10-03,2020-10-06,1",
            "2020-10-04,2020-10-08,2",
            "2020-10-01,2020-10-05,1",
    })
    void shouldGetGroupTrainingsByTypeWithoutParticipantsWithinDates(
            String startDate, String endDate, int numberOfTrainingsWithinDates
    ) throws Exception {
        testDatabaseState();

        uri = new URI("http://localhost:" + port + "/group/public/type/" + trainingTypeId
                + "?startDate=" + startDate + "&endDate=" + endDate);
        ResponseEntity<JsonNode> responseEntity = performRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(numberOfTrainingsWithinDates);

        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupTrainingWithoutParticipantsDTO> groupTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });
        assertThat(groupTrainingDTOList.size()).isEqualTo(numberOfTrainingsWithinDates);

        testDatabaseState();
    }

    @ParameterizedTest
    @CsvSource({
            "2020-10-01,2020-11-03,3",
            "2020-10-01,2020-10-05,1",
            "2020-10-09,2020-10-10,0",
            "2020-10-04,2020-10-07,1",
            "2020-10-03,2020-10-06,1",
            "2020-10-04,2020-10-08,2",
            "2020-10-01,2020-10-05,1",
    })
    void shouldGetGroupTrainingsByTypeWithParticipantsWithinDates(
            String startDate, String endDate, int numberOfTrainingsWithinDates
    ) throws Exception {
        testDatabaseState();

        uri = new URI("http://localhost:" + port + "/group/type/" + trainingTypeId
                + "?startDate=" + startDate + "&endDate=" + endDate);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(numberOfTrainingsWithinDates);

        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupTrainingDTO> groupTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });
        assertThat(groupTrainingDTOList.size()).isEqualTo(numberOfTrainingsWithinDates);

        testDatabaseState();
    }

}
