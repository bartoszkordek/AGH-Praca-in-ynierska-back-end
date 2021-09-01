package com.healthy.gym.trainings.controller.userNextTraining.integrationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.FixedClockConfig;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.*;
import com.healthy.gym.trainings.enums.GymRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
@Tag("integration")
public class GetUserNextTrainingIntegrationTest {

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

    @LocalServerPort
    private Integer port;

    private String userToken;
    private String managerToken;
    private UserDocument user;
    private String userId;
    private String managerId;

    private UserDocument userDocument;
    private UserDocument trainerDocument;

    private String groupTrainingId1;
    private String groupTrainingId2;
    private String groupTrainingId3;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getManagerToken(managerId);

        var now = LocalDateTime.now();

        String userName = "Jan";
        String userSurname = "Kowalski";
        userDocument = new UserDocument();
        userDocument.setName(userName);
        userDocument.setSurname(userSurname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocument);

        String trainerId = UUID.randomUUID().toString();
        String trainerName = "Tadeusz";
        String trainerSurname = "Trener";
        trainerDocument = new UserDocument();
        trainerDocument.setName(trainerName);
        trainerDocument.setSurname(trainerSurname);
        trainerDocument.setUserId(trainerId);
        trainerDocument.setGymRoles(List.of(GymRole.TRAINER));

        mongoTemplate.save(trainerDocument);

        String trainingTypeId1 = UUID.randomUUID().toString();
        String trainingName1 = "Pilates";
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(trainingTypeId1, trainingName1);
        mongoTemplate.save(trainingTypeDocument1);

        String trainingTypeId2 = UUID.randomUUID().toString();
        String trainingName2 = "Rowery";
        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(trainingTypeId2, trainingName2);
        mongoTemplate.save(trainingTypeDocument2);

        String trainingTypeId3 = UUID.randomUUID().toString();
        String trainingName3 = "Joga";
        TrainingTypeDocument trainingTypeDocument3 = new TrainingTypeDocument(trainingTypeId3, trainingName3);
        mongoTemplate.save(trainingTypeDocument3);

        String locationId1 = UUID.randomUUID().toString();
        String locationName1 = "Sala nr 2";
        LocationDocument locationDocument1 = new LocationDocument(locationId1, locationName1);
        mongoTemplate.save(locationDocument1);

        String locationId2 = UUID.randomUUID().toString();
        String locationName2 = "Sala nr 3";
        LocationDocument locationDocument2 = new LocationDocument(locationId2, locationName2);
        mongoTemplate.save(locationDocument2);

        groupTrainingId1 = UUID.randomUUID().toString();
        GroupTrainingDocument groupTrainingDocument1 = new GroupTrainingDocument(
                groupTrainingId1,
                trainingTypeDocument1,
                List.of(trainerDocument),
                now.plusDays(10),
                now.plusDays(10).plusMinutes(30),
                locationDocument1,
                20,
                List.of(userDocument),
        null
        );

        mongoTemplate.save(groupTrainingDocument1);


        groupTrainingId2 = UUID.randomUUID().toString();
        GroupTrainingDocument groupTrainingDocument2 = new GroupTrainingDocument(
                groupTrainingId2,
                trainingTypeDocument2,
                List.of(trainerDocument),
                now.plusDays(1),
                now.plusDays(1).plusHours(1),
                locationDocument2,
                15,
                List.of(userDocument),
                null
        );

        mongoTemplate.save(groupTrainingDocument2);


        groupTrainingId3 = UUID.randomUUID().toString();
        GroupTrainingDocument groupTrainingDocument3 = new GroupTrainingDocument(
                groupTrainingId3,
                trainingTypeDocument3,
                List.of(trainerDocument),
                now.plusDays(5),
                now.plusDays(5).plusHours(1),
                locationDocument1,
                25,
                List.of(userDocument),
                null
        );

        mongoTemplate.save(groupTrainingDocument3);

    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    @Nested
    class ShouldGetUserNextTraining{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetUserNextTraining_whenValidUserId(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/user/" + userId + "/next");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();

            assertThat(responseEntity.getBody().get("id")).isNotNull();
            assertThat(responseEntity.getBody().get("id").textValue())
                    .isEqualTo(groupTrainingId2);
            assertThat(responseEntity.getBody().get("title").textValue())
                    .isEqualTo("Rowery");
            assertThat(responseEntity.getBody().get("startDate").textValue()).isNotNull();
            assertThat(responseEntity.getBody().get("location").textValue())
                    .isEqualTo("Sala nr 3");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetUserNextTraining_whenValidUserId_addedIndividualTrainingBeforeGroup(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            //before
            var now = LocalDateTime.now();

            String individualTrainingTypeId = UUID.randomUUID().toString();
            String individualTrainingName = "Trening indywidualny";
            TrainingTypeDocument individualTrainingTypeDocument = new TrainingTypeDocument(
                    individualTrainingTypeId,
                    individualTrainingName
            );
            mongoTemplate.save(individualTrainingTypeDocument);

            String locationId10 = UUID.randomUUID().toString();
            String locationName10 = "Sala nr 10";
            LocationDocument locationDocument10 = new LocationDocument(locationId10, locationName10);
            mongoTemplate.save(locationDocument10);

            String individualTrainingId = UUID.randomUUID().toString();
            IndividualTrainingDocument individualTrainingDocument = new IndividualTrainingDocument(
                    individualTrainingId,
                    individualTrainingTypeDocument,
                    List.of(userDocument),
                    List.of(trainerDocument),
                    now.plusHours(2),
                    now.plusHours(3),
                    locationDocument10,
                    "Komentarz"
            );

            mongoTemplate.save(individualTrainingDocument);

            URI uri = new URI("http://localhost:" + port + "/user/" + userId + "/next");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();

            assertThat(responseEntity.getBody().get("id")).isNotNull();
            assertThat(responseEntity.getBody().get("id").textValue())
                    .isEqualTo(individualTrainingId);
            assertThat(responseEntity.getBody().get("title").textValue())
                    .isEqualTo("Trening indywidualny");
            assertThat(responseEntity.getBody().get("startDate").textValue()).isNotNull();
            assertThat(responseEntity.getBody().get("location").textValue())
                    .isEqualTo("Sala nr 10");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetUserNextTraining_whenValidUserId_addedIndividualTrainingAfterGroup(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            //before
            var now = LocalDateTime.now();

            String individualTrainingTypeId = UUID.randomUUID().toString();
            String individualTrainingName = "Trening indywidualny";
            TrainingTypeDocument individualTrainingTypeDocument = new TrainingTypeDocument(
                    individualTrainingTypeId,
                    individualTrainingName
            );
            mongoTemplate.save(individualTrainingTypeDocument);

            String locationId10 = UUID.randomUUID().toString();
            String locationName10 = "Sala nr 10";
            LocationDocument locationDocument10 = new LocationDocument(locationId10, locationName10);
            mongoTemplate.save(locationDocument10);

            String individualTrainingId = UUID.randomUUID().toString();
            IndividualTrainingDocument individualTrainingDocument = new IndividualTrainingDocument(
                    individualTrainingId,
                    individualTrainingTypeDocument,
                    List.of(userDocument),
                    List.of(trainerDocument),
                    now.plusDays(1).plusHours(1),
                    now.plusDays(1).plusHours(2),
                    locationDocument10,
                    "Komentarz"
            );

            mongoTemplate.save(individualTrainingDocument);

            URI uri = new URI("http://localhost:" + port + "/user/" + userId + "/next");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();

            assertThat(responseEntity.getBody().get("id")).isNotNull();
            assertThat(responseEntity.getBody().get("id").textValue())
                    .isEqualTo(groupTrainingId2);
            assertThat(responseEntity.getBody().get("title").textValue())
                    .isEqualTo("Rowery");
            assertThat(responseEntity.getBody().get("startDate").textValue()).isNotNull();
            assertThat(responseEntity.getBody().get("location").textValue())
                    .isEqualTo("Sala nr 3");
        }
    }

    @Nested
    class ShouldNotGetUserNextTraining{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetNextTraining_whenInvalidUserId(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidUserId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/user/" + invalidUserId + "/next");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.not.found.user.id");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(400);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetNextTraining_whenNoTrainings(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            mongoTemplate.dropCollection(GroupTrainingDocument.class);
            mongoTemplate.dropCollection(IndividualTrainingDocument.class);

            URI uri = new URI("http://localhost:" + port + "/user/" + userId + "/next");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.user.next.training.not.found");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(404);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @Nested
        class Authentication{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetNextTraining_whenNoToken(TestCountry country)
                    throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                String invalidUserId = UUID.randomUUID().toString();

                URI uri = new URI("http://localhost:" + port + "/user/" + invalidUserId + "/next");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.GET, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }


            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetNextTraining_whenOtherUserToken(TestCountry country)
                    throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                String otherUserId = UUID.randomUUID().toString();
                String otherUserToken = tokenFactory.getUserToken(otherUserId);

                URI uri = new URI("http://localhost:" + port + "/user/" + userId + "/next");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", otherUserToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                String expectedMessage = messages.get("exception.access.denied");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.GET, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }
        }
    }
}
