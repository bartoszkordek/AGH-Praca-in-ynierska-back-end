package com.healthy.gym.trainings.controller.groupTrainingController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.model.request.CreateGroupTrainingRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenGetGroupTrainingIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;

    @LocalServerPort
    private Integer port;
    private String managerToken;
    private String requestContent;

    private String trainingTypeId;
    private String trainerId1;
    private String trainerId2;
    private String locationId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        trainingTypeId = UUID.randomUUID().toString();
        trainerId1 = UUID.randomUUID().toString();
        trainerId2 = UUID.randomUUID().toString();
        locationId = UUID.randomUUID().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        requestContent = objectMapper.writeValueAsString(createTestRequest());

        TrainingTypeDocument trainingType = new TrainingTypeDocument(
                trainingTypeId,
                "Test training name",
                null,
                null,
                null
        );
        mongoTemplate.save(trainingType);

        UserDocument trainer1 = new UserDocument(
                "TrainerName1",
                "TrainerSurname1",
                null,
                null,
                null,
                trainerId1
        );
        trainer1.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        mongoTemplate.save(trainer1);

        UserDocument trainer2 = new UserDocument(
                "TrainerName2",
                "TrainerSurname2",
                null,
                null,
                null,
                trainerId2
        );
        trainer2.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        mongoTemplate.save(trainer2);

        LocationDocument location = new LocationDocument(
                locationId,
                "TestLocationName"
        );
        mongoTemplate.save(location);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    private CreateGroupTrainingRequest createTestRequest() {
        CreateGroupTrainingRequest request = new CreateGroupTrainingRequest();
        request.setTrainingTypeId(trainingTypeId);
        request.setTrainerIds(List.of(trainerId1, trainerId2));
        request.setStartDate("2020-10-10T16:00");
        request.setEndDate("2020-10-10T16:30");
        request.setLocationId(locationId);
        request.setLimit(20);
        return request;
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetGroupTrainings(TestCountry country) throws Exception {

        //before
        Locale testedLocale = convertEnumToLocale(country);

        URI createUri = new URI("http://localhost:" + port + "/group");

        HttpHeaders createHeaders = new HttpHeaders();
        createHeaders.set("Accept-Language", testedLocale.toString());
        createHeaders.set("Authorization", managerToken);
        createHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> createRequest = new HttpEntity<>(requestContent, createHeaders);

        ResponseEntity<JsonNode> createResponseEntity = restTemplate
                .exchange(createUri, HttpMethod.POST, createRequest, JsonNode.class);

        //when
        String startDate = "2020-01-01";
        String endDate = "2021-12-31";

        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("Accept-Language", testedLocale.toString());
        getHeaders.set("Authorization", managerToken);

        HttpEntity<Object> getRequest = new HttpEntity<>(null, getHeaders);

        //PRIVATE
        URI getUri = new URI("http://localhost:" + port + "/group" +
                "?startDate="+startDate+"&endDate="+endDate);

        ResponseEntity<JsonNode> getResponseEntity = restTemplate
                .exchange(getUri, HttpMethod.GET, getRequest, JsonNode.class);

        JsonNode trainings = getResponseEntity.getBody().get("data");

        //then
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody().has("data"));
        assertThat(trainings.get(0).get("id")).isNotNull();
        assertThat(trainings.get(0).get("title").textValue()).isEqualTo("Test training name");
        assertThat(trainings.get(0).get("startDate").textValue()).isEqualTo("2020-10-10T16:00");
        assertThat(trainings.get(0).get("endDate").textValue()).isEqualTo("2020-10-10T16:30");
        assertThat(trainings.get(0).get("allDay").booleanValue()).isFalse();
        assertThat(trainings.get(0).get("location").textValue()).isEqualTo("TestLocationName");

        //trainer1
        assertThat(trainings.get(0).get("trainers").get(0).get("name").textValue())
                .isEqualTo("TrainerName1");
        assertThat(trainings.get(0).get("trainers").get(0).get("surname").textValue())
                .isEqualTo("TrainerSurname1");

        //trainer2
        assertThat(trainings.get(0).get("trainers").get(1).get("name").textValue())
                .isEqualTo("TrainerName2");
        assertThat(trainings.get(0).get("trainers").get(1).get("surname").textValue())
                .isEqualTo("TrainerSurname2");

        //participants
        assertThat(trainings.get(0).has("participants")).isTrue();
        assertThat(trainings.get(0).get("participants").get("basicList").isArray());
        assertThat(trainings.get(0).get("participants").get("reserveList").isArray());


        //PUBLIC
        URI getPublicUri = new URI("http://localhost:" + port + "/group/public" +
                "?startDate="+startDate+"&endDate="+endDate);

        ResponseEntity<JsonNode> getPublicResponseEntity = restTemplate
                .exchange(getPublicUri, HttpMethod.GET, getRequest, JsonNode.class);

        System.out.println(getPublicResponseEntity.getBody());

        JsonNode publicTrainings = getPublicResponseEntity.getBody().get("data");

        //then
        assertThat(getResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponseEntity.getBody().has("data"));
        assertThat(publicTrainings.get(0).get("id")).isNotNull();
        assertThat(publicTrainings.get(0).get("title").textValue()).isEqualTo("Test training name");
        assertThat(publicTrainings.get(0).get("startDate").textValue()).isEqualTo("2020-10-10T16:00");
        assertThat(publicTrainings.get(0).get("endDate").textValue()).isEqualTo("2020-10-10T16:30");
        assertThat(publicTrainings.get(0).get("allDay").booleanValue()).isFalse();
        assertThat(publicTrainings.get(0).get("location").textValue()).isEqualTo("TestLocationName");

        //trainer1
        assertThat(publicTrainings.get(0).get("trainers").get(0).get("name").textValue())
                .isEqualTo("TrainerName1");
        assertThat(publicTrainings.get(0).get("trainers").get(0).get("surname").textValue())
                .isEqualTo("TrainerSurname1");

        //trainer2
        assertThat(publicTrainings.get(0).get("trainers").get(1).get("name").textValue())
                .isEqualTo("TrainerName2");
        assertThat(publicTrainings.get(0).get("trainers").get(1).get("surname").textValue())
                .isEqualTo("TrainerSurname2");

        //participants
        assertThat(publicTrainings.get(0).has("participants")).isFalse();
    }

}
