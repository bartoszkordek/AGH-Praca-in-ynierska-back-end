package com.healthy.gym.account.controller.trainerController;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.data.document.ImageDocument;
import com.healthy.gym.account.data.document.TrainerDocument;
import com.healthy.gym.account.data.document.TrainingTypeDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.enums.GymRole;
import org.bson.types.Binary;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.*;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetTrainerProfileByUserIdIntegrationTest {
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
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userId1;
    private String userId2;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        Resource imageResource1 = new ClassPathResource("testImagess/shiba_inu_smile_1.jpg");

        ImageDocument imageDocument1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource1)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedTrainingTypeImageDocument1 = mongoTemplate.save(imageDocument1);

        String trainingTypeId1 = UUID.randomUUID().toString();
        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                trainingTypeId1,
                "Test name",
                "Test description",
                LocalTime.of(1, 30),
                savedTrainingTypeImageDocument1,
                "http://localhost:8020/trainings/trainingType/image" + trainingTypeId1 + "?version=test1"
        );
        mongoTemplate.save(trainingTypeDocument1);

        Resource imageResourceTrainer1 = new ClassPathResource("mem.jpg");

        ImageDocument imageDocumentTrainer1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResourceTrainer1)),
                MediaType.IMAGE_JPEG_VALUE
        );
        ImageDocument savedImageDocumentTrainer1 = mongoTemplate.save(imageDocumentTrainer1);

        userId1 = UUID.randomUUID().toString();
        UserDocument userDocument1 = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                userId1
        );
        userDocument1.setGymRoles(List.of(GymRole.TRAINER));

        mongoTemplate.save(userDocument1);


        TrainerDocument trainerDocument1 = new TrainerDocument(
                userDocument1,
                List.of(savedImageDocumentTrainer1),
                List.of("http://localhost:8020/account/trainer/image/" + userId1 + "?version=test1"),
                "Sample synopsis 1",
                "Sample full 1",
                List.of(trainingTypeDocument1)
        );

        mongoTemplate.save(trainerDocument1);

        Resource imageResource2 = new ClassPathResource("testImagess/shiba_inu_smile_2.jpg");

        ImageDocument trainingTypeImageDocument2 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource2)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedTrainingTypeImageDocument2 = mongoTemplate.save(trainingTypeImageDocument2);

        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                UUID.randomUUID().toString(),
                "Test name2",
                "Test description2",
                LocalTime.of(2, 30),
                savedTrainingTypeImageDocument2,
                "http://localhost:8020/trainings/trainingType/image/" + trainingTypeId1 + "?version=test1"
        );

        mongoTemplate.save(trainingTypeDocument2);

        userId2 = UUID.randomUUID().toString();
        UserDocument userDocument2 = new UserDocument("Paweł",
                "Borsuk",
                "pawel.borsuk@test.com",
                "556 777 888",
                bCryptPasswordEncoder.encode("password1235"),
                userId2
        );
        userDocument2.setGymRoles(List.of(GymRole.TRAINER));

        mongoTemplate.save(userDocument2);

        Resource imageResourceTrainer2 = new ClassPathResource("mem.jpg");

        ImageDocument imageDocumentTrainer2 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResourceTrainer2)),
                MediaType.IMAGE_JPEG_VALUE
        );
        ImageDocument savedImageDocumentTrainer2 = mongoTemplate.save(imageDocumentTrainer2);

        TrainerDocument trainerDocument2 = new TrainerDocument(
                userDocument2,
                List.of(savedImageDocumentTrainer2),
                List.of("http://localhost:8020/account/trainer/image/" + userId2 + "?version=test1"),
                "Sample synopsis 2",
                "Sample full 2",
                List.of(trainingTypeDocument2)
        );

        mongoTemplate.save(trainerDocument2);
    }

    private byte[] getImageBytes(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        return inputStream.readAllBytes();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(ImageDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(TrainerDocument.class);
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        return headers;
    }

    private HttpEntity<Object> getRequestEntity(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        return new HttpEntity<>(null, headers);
    }

    private String getExpectedImageBase64(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] imageBytes = inputStream.readAllBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(imageBytes);
    }

    @Nested
    class ShouldAcceptRequestAndShouldResponseWithProperTrainer {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusOK(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperMediaType(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotArray(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().isArray()).isFalse();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNoErrors(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("errors")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotNullUserId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get("userId").textValue()).isEqualTo(userId2);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperName(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("name")).isNotNull();
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Paweł");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperSurname(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("surname")).isNotNull();
            assertThat(responseEntity.getBody().get("surname").textValue()).isEqualTo("Borsuk");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().
                    get("description")).isNotNull();
            assertThat(responseEntity.getBody().
                    get("description").get("synopsis").textValue()).isEqualTo("Sample synopsis 2");
            assertThat(responseEntity.getBody().
                    get("description").get("full").textValue()).isEqualTo("Sample full 2");
            assertThat(responseEntity.getBody().
                    get("description").get("trainings")).isNotNull();
            assertThat(responseEntity.getBody().
                    get("description").get("trainings").get(0)).isNotNull();
            assertThat(responseEntity.getBody().
                    get("description").get("trainings").get(0).get("trainingId")).isNotNull();
            assertThat(responseEntity.getBody().
                    get("description").get("trainings").get(0).get("title").textValue()).isEqualTo("Test name2");

        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageUrl(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("images")).isNotNull();
            assertThat(responseEntity.getBody().get("images").get(0)).isNotNull();
        }


        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port +"/trainer/"+userId2);
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class ShouldAcceptRequestAndShouldResponseWithNotFoundTrainerProfile {

        @BeforeEach
        void setUp() {
            mongoTemplate.dropCollection(ImageDocument.class);
            mongoTemplate.dropCollection(TrainingTypeDocument.class);
            mongoTemplate.dropCollection(UserDocument.class);
            mongoTemplate.dropCollection(TrainerDocument.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusNotFound(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnExpectedMessage(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            String expectedMessage = messages.get("exception.no.user.found");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port +"/trainer/"+UUID.randomUUID().toString());
            return restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }
}
