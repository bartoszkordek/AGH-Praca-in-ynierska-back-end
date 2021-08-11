package com.healthy.gym.trainings.controller.group.training.user.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
class GetAllGroupTrainingsByUserIdIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestDocumentUtilComponent testUtil;

    @LocalServerPort
    private Integer port;

    private String userToken;
    private UserDocument user;
    private String userId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        user = testUtil.saveAndGetTestUser(userId);

        testUtil.createTestGroupTraining(
                user, "2020-10-01T11:30", "2020-10-01T12:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-02T12:30", "2020-10-02T13:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-03T13:30", "2020-10-03T14:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-04T14:30", "2020-10-04T15:00", true, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-05T15:30", "2020-10-05T16:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-06T16:30", "2020-10-06T17:00", true, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-07T17:30", "2020-10-07T18:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-08T18:30", "2020-10-08T19:00", false, true);
        testUtil.createTestGroupTraining(
                user, "2020-10-09T19:30", "2020-10-09T20:00", false, false);
        testUtil.createTestGroupTraining(
                user, "2020-10-10T20:30", "2020-10-10T21:00", true, false);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    private URI getUri(Integer port, String userId, String startDate, String endDate) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/group/trainings/" + userId
                + "?startDate=" + startDate + "&endDate=" + endDate);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetAllUserTrainingsWithinSelectedDates1(TestCountry country) throws Exception {
        List<GroupTrainingDocument> groupTrainingList = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainingList.size()).isEqualTo(10);

        Locale testedLocale = convertEnumToLocale(country);
        URI uri = getUri(port, userId, "2020-10-01", "2020-10-04");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(2);

        checkIfUserIsPresentAsParticipantInEachTraining(body);
    }

    private void checkIfUserIsPresentAsParticipantInEachTraining(JsonNode body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupTrainingDTO> groupTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });

        List<List<BasicUserInfoDTO>> listOfParticipantsList = groupTrainingDTOList
                .stream()
                .map(groupTrainingDTO -> {
                    var basicList = groupTrainingDTO.getParticipants().getBasicList();
                    var reserveList = groupTrainingDTO.getParticipants().getReserveList();

                    List<BasicUserInfoDTO> participants = new ArrayList<>();
                    participants.addAll(basicList);
                    participants.addAll(reserveList);
                    return participants;
                })
                .collect(Collectors.toList());

        for (List<BasicUserInfoDTO> participants : listOfParticipantsList) {
            var dtoList = participants
                    .stream()
                    .filter(user -> user.getUserId().equals(userId))
                    .collect(Collectors.toList());

            BasicUserInfoDTO userInfoDTO = dtoList.get(0);

            assertThat(userInfoDTO.getUserId()).isEqualTo(userId);
            assertThat(userInfoDTO.getName()).isEqualTo(user.getName());
            assertThat(userInfoDTO.getSurname()).isEqualTo(user.getSurname());
        }
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetAllUserTrainingsWithinSelectedDates2(TestCountry country) throws Exception {
        List<GroupTrainingDocument> groupTrainingList = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainingList.size()).isEqualTo(10);

        Locale testedLocale = convertEnumToLocale(country);
        URI uri = getUri(port, userId, "2020-10-02", "2020-10-10");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(5);

        checkIfUserIsPresentAsParticipantInEachTraining(body);
    }
}
