package com.healthy.gym.trainings.controller.group.training.manager.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.enums.GymRole;
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
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class RemoveGroupTrainingIntegrationTest {

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

    private String groupTrainingId;
    private String trainingTypeId1;
    private String trainerId1;
    private String locationId1;
    private String testUserId1;
    private String testUserId2;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        managerToken = tokenFactory.getManagerToken(UUID.randomUUID().toString());
        prepareCurrentGroupTrainingDocumentForTesting();
    }

    void prepareCurrentGroupTrainingDocumentForTesting() {
        trainingTypeId1 = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType1 = getTrainingType1();
        TrainingTypeDocument trainingType1Saved = mongoTemplate.save(trainingType1);

        trainerId1 = UUID.randomUUID().toString();
        UserDocument trainer1 = getTestTrainer1();
        UserDocument trainer1Saved = mongoTemplate.save(trainer1);

        locationId1 = UUID.randomUUID().toString();
        LocationDocument location1 = getLocation1();
        LocationDocument location1Saved = mongoTemplate.save(location1);

        testUserId1 = UUID.randomUUID().toString();
        UserDocument user1 = getTestUser1();
        UserDocument user1Saved = mongoTemplate.save(user1);

        testUserId2 = UUID.randomUUID().toString();
        UserDocument user2 = getTestUser2();
        UserDocument user2Saved = mongoTemplate.save(user2);

        groupTrainingId = UUID.randomUUID().toString();
        GroupTrainingDocument groupTrainingDocument = new GroupTrainingDocument(
                groupTrainingId,
                trainingType1Saved,
                List.of(trainer1Saved),
                LocalDateTime.parse("2020-10-10T15:30"),
                LocalDateTime.parse("2020-10-10T16:00"),
                location1Saved,
                20,
                List.of(user1Saved),
                List.of(user2Saved)
        );
        mongoTemplate.save(groupTrainingDocument);
    }

    private TrainingTypeDocument getTrainingType1() {
        var trainingType = new TrainingTypeDocument();
        trainingType.setTrainingTypeId(trainingTypeId1);
        trainingType.setName("Test training name1");
        return trainingType;
    }

    private UserDocument getTestTrainer1() {
        var trainer = new UserDocument();
        trainer.setUserId(trainerId1);
        trainer.setName("TrainerName1");
        trainer.setSurname("TrainerSurname1");
        trainer.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return trainer;
    }

    private UserDocument getTestUser1() {
        var trainer = new UserDocument();
        trainer.setUserId(testUserId1);
        trainer.setName("UserName1");
        trainer.setSurname("UserSurname1");
        trainer.setGymRoles(List.of(GymRole.USER));
        return trainer;
    }

    private UserDocument getTestUser2() {
        var trainer = new UserDocument();
        trainer.setUserId(testUserId2);
        trainer.setName("UserName2");
        trainer.setSurname("UserSurname2");
        trainer.setGymRoles(List.of(GymRole.USER));
        return trainer;
    }

    private LocationDocument getLocation1() {
        return new LocationDocument(
                locationId1,
                "TestLocationName1"
        );
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateGroupTraining(TestCountry country) throws Exception {
        testCurrentGroupTrainingStoredInDB();

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/group/" + groupTrainingId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("request.delete.training.success");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).get("message")
                .textValue()).isEqualTo(expectedMessage);

        JsonNode training = responseEntity.getBody().get("training");
        testTrainingDetails(training);

        JsonNode trainer = training.get("trainers").get(0);
        testUserDetails(trainer, trainerId1, "TrainerName1", "TrainerSurname1");

        JsonNode participants = training.get("participants");
        assertThat(participants.get("basicList").isArray()).isTrue();
        assertThat(participants.get("reserveList").isArray()).isTrue();

        JsonNode user1 = participants.get("basicList").get(0);
        testUserDetails(user1, testUserId1, "UserName1", "UserSurname1");

        JsonNode user2 = participants.get("reserveList").get(0);
        testUserDetails(user2, testUserId2, "UserName2", "UserSurname2");

        testUpdatedGroupTrainingStoredInDB();
    }

    private void testCurrentGroupTrainingStoredInDB() {
        List<GroupTrainingDocument> groupTrainings = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainings.size()).isEqualTo(1);

        var trainingTypes = mongoTemplate.findAll(TrainingTypeDocument.class);
        assertThat(trainingTypes.size()).isEqualTo(1);

        var users = mongoTemplate.findAll(UserDocument.class);
        assertThat(users.size()).isEqualTo(3);

        var locations = mongoTemplate.findAll(LocationDocument.class);
        assertThat(locations.size()).isEqualTo(1);

        GroupTrainingDocument groupTraining = groupTrainings.get(0);

        assertThat(groupTraining.getGroupTrainingId()).isEqualTo(groupTrainingId);

        TrainingTypeDocument trainingTypeDocument = groupTraining.getTraining();
        assertThat(trainingTypeDocument.getTrainingTypeId()).isEqualTo(trainingTypeId1);

        UserDocument trainer = groupTraining.getTrainers().get(0);
        assertThat(trainer.getUserId()).isEqualTo(trainerId1);

        assertThat(groupTraining.getStartDate()).isEqualTo(LocalDateTime.parse("2020-10-10T15:30"));
        assertThat(groupTraining.getEndDate()).isEqualTo(LocalDateTime.parse("2020-10-10T16:00"));

        LocationDocument locationDocument = groupTraining.getLocation();
        assertThat(locationDocument.getLocationId()).isEqualTo(locationId1);

        int limit = groupTraining.getLimit();
        assertThat(limit).isEqualTo(20);

        UserDocument user1 = groupTraining.getBasicList().get(0);
        assertThat(user1.getUserId()).isEqualTo(testUserId1);

        UserDocument user2 = groupTraining.getReserveList().get(0);
        assertThat(user2.getUserId()).isEqualTo(testUserId2);
    }

    private void testTrainingDetails(JsonNode training) {
        assertThat(training.get("id").textValue()).isEqualTo(groupTrainingId);
        assertThat(training.get("title").textValue()).isEqualTo("Test training name1");
        assertThat(training.get("startDate").textValue()).isEqualTo("2020-10-10T15:30");
        assertThat(training.get("endDate").textValue()).isEqualTo("2020-10-10T16:00");
        assertThat(training.get("allDay").booleanValue()).isFalse();
        assertThat(training.get("location").textValue()).isEqualTo("TestLocationName1");
    }

    private void testUserDetails(JsonNode user, String expectedId, String expectedName, String expectedSurname) {
        assertThat(user.get("userId").textValue()).isEqualTo(expectedId);
        assertThat(user.get("name").textValue()).isEqualTo(expectedName);
        assertThat(user.get("surname").textValue()).isEqualTo(expectedSurname);
        assertThat(user.get("avatar")).isNull();
    }


    private void testUpdatedGroupTrainingStoredInDB() {
        List<GroupTrainingDocument> groupTrainings = mongoTemplate.findAll(GroupTrainingDocument.class);
        assertThat(groupTrainings.size()).isZero();

        var trainingTypes = mongoTemplate.findAll(TrainingTypeDocument.class);
        assertThat(trainingTypes.size()).isEqualTo(1);

        var users = mongoTemplate.findAll(UserDocument.class);
        assertThat(users.size()).isEqualTo(3);

        var locations = mongoTemplate.findAll(LocationDocument.class);
        assertThat(locations.size()).isEqualTo(1);
    }
}
