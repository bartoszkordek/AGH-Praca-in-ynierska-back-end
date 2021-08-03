package com.healthy.gym.trainings.controller.group.training.manager.integrationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.enums.GymRole;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
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
class WhenCreateGroupTrainingIntegrationTest {

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
        requestContent = objectMapper.writeValueAsString(getTestRequest());

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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateGroupTraining(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/group");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("request.create.training.success");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);

        System.out.println(responseEntity);

        JsonNode training = responseEntity.getBody().get("training");
        assertThat(training.get("id")).isNotNull();
        assertThat(training.get("title").textValue()).isEqualTo("Test training name");
        assertThat(training.get("startDate").textValue()).isEqualTo("2020-10-10T16:00");
        assertThat(training.get("endDate").textValue()).isEqualTo("2020-10-10T16:30");
        assertThat(training.get("allDay").booleanValue()).isFalse();
        assertThat(training.get("location").textValue()).isEqualTo("TestLocationName");

        JsonNode firstTrainer = training.get("trainers").get(0);
        assertThat(firstTrainer.get("userId").textValue()).isEqualTo(trainerId1);
        assertThat(firstTrainer.get("name").textValue()).isEqualTo("TrainerName1");
        assertThat(firstTrainer.get("surname").textValue()).isEqualTo("TrainerSurname1");
        assertThat(firstTrainer.get("avatar")).isNull();

        JsonNode secondTrainer = training.get("trainers").get(1);
        assertThat(secondTrainer.get("userId").textValue()).isEqualTo(trainerId2);
        assertThat(secondTrainer.get("name").textValue()).isEqualTo("TrainerName2");
        assertThat(secondTrainer.get("surname").textValue()).isEqualTo("TrainerSurname2");
        assertThat(secondTrainer.get("avatar")).isNull();

        JsonNode participants = training.get("participants");
        assertThat(participants.get("basicList").isArray()).isTrue();
        assertThat(participants.get("reserveList").isArray()).isTrue();

        List<GroupTrainingDocument> groupTrainings = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainings.size()).isEqualTo(1);
    }

}
