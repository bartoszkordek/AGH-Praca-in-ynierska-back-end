package com.healthy.gym.account.controller.trainerController;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.TrainerDocument;
import com.healthy.gym.account.data.document.TrainingTypeDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.pojo.request.TrainerRequest;
import com.healthy.gym.account.configuration.tests.TestCountry;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.healthy.gym.account.data.document.ImageDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class CreateTrainerProfileIntegrationTest {

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
    @Autowired
    private TestRoleTokenFactory tokenFactory;

    private String adminToken;
    private String userId;
    private TrainerRequest trainerRequest;
    private Resource imageResource;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        String trainingTypeId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingTypeDocument = new TrainingTypeDocument();
        trainingTypeDocument.setTrainingTypeId(trainingTypeId);
        trainingTypeDocument.setName("Test training type");

        mongoTemplate.save(trainingTypeDocument);

        userId = UUID.randomUUID().toString();
        UserDocument userDocument = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                userId
        );
        userDocument.setGymRoles(List.of(GymRole.TRAINER));

        mongoTemplate.save(userDocument);

        trainerRequest = new TrainerRequest();
        trainerRequest.setUserId(userId);
        trainerRequest.setSynopsis("Sample synopsis");
        trainerRequest.setFull("Sample full");
        trainerRequest.setTrainingIds(List.of(trainingTypeId));

        imageResource = new ClassPathResource("mem.jpg");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainerDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(ImageDocument.class);
    }

    @Test
    void shouldReturnNewTrainerProfileWithImageUrl() throws URISyntaxException, IOException {
        testDatabase(0,1, 1, 0);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getURI(),
                HttpMethod.POST,
                getRequestEntity(),
                JsonNode.class
        );

        System.out.println(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        TestCountry country = TestCountry.ENGLAND;
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("trainer.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();
        JsonNode trainer = body.get("trainer");
        assertThat(trainer.get("userId").textValue()).isNotNull();
        assertThat(trainer.get("userId").textValue()).isEqualTo(userId);
        assertThat(trainer.get("name").textValue()).isEqualTo("Jan");
        assertThat(trainer.get("surname").textValue()).isEqualTo("Kowalski");
        assertThat(trainer.get("images")).isNotNull();
        assertThat(trainer.get("images").get(0)).isNotNull();
        assertThat(trainer.get("description").get("synopsis")).isNotNull();
        assertThat(trainer.get("description").get("synopsis").textValue()).isEqualTo("Sample synopsis");
        assertThat(trainer.get("description").get("full")).isNotNull();
        assertThat(trainer.get("description").get("full").textValue()).isEqualTo("Sample full");
        assertThat(trainer.get("description").get("trainings")).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0)).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0).get("trainingId")).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0).get("title").textValue())
                .isEqualTo("Test training type");
        assertThat(trainer.get("images").isEmpty()).isFalse();

        String image = trainer.get("images").get(0).textValue();
        assertThat(image).startsWith("http://localhost:8020/account/trainer/image/");
        byte[] imageBytes = imageResource.getInputStream().readAllBytes();
        assertThat(image).endsWith("?version=" + DigestUtils.md5DigestAsHex(imageBytes));

        testDatabase(1,1,1, 1);
    }

    private void testDatabase(
            int expectedNumberOfTrainers,
            int expectedNumberOfUsers,
            int expectedNumberOfTrainingTypes,
            int expectedNumberOfImages
    ) {
        var trainer = mongoTemplate.findAll(TrainerDocument.class);
        var user = mongoTemplate.findAll(UserDocument.class);
        var trainings = mongoTemplate.findAll(TrainingTypeDocument.class);
        var images = mongoTemplate.findAll(ImageDocument.class);

        assertThat(trainer.size()).isEqualTo(expectedNumberOfTrainers);
        assertThat(user.size()).isEqualTo(expectedNumberOfUsers);
        assertThat(trainings.size()).isEqualTo(expectedNumberOfTrainingTypes);
        assertThat(images.size()).isEqualTo(expectedNumberOfImages);
    }

    private HttpEntity<Object> getRequestEntity() {
        HttpHeaders headers = getHeaders();
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("image", getImagePart());
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }

    private HttpEntity<Object> getImagePart() {
        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new HttpEntity<>(imageResource, imageHeaders);
    }

    private HttpEntity<Object> getBodyPart() {
        HttpHeaders bodyHeaders = new HttpHeaders();
        bodyHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(trainerRequest, bodyHeaders);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private URI getURI() throws URISyntaxException {
        return new URI("http://localhost:" + port +"/trainer/"+userId);
    }

    @Test
    void shouldReturnNewTrainerProfileWithoutImage() throws URISyntaxException {
        testDatabase(0,1, 1,0);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getURI(),
                HttpMethod.POST,
                getRequestEntityWithoutImage(),
                JsonNode.class
        );

        System.out.println(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        TestCountry country = TestCountry.ENGLAND;
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("trainer.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();
        JsonNode trainer = body.get("trainer");
        assertThat(trainer.get("userId").textValue()).isNotNull();
        assertThat(trainer.get("userId").textValue()).isEqualTo(userId);
        assertThat(trainer.get("name").textValue()).isEqualTo("Jan");
        assertThat(trainer.get("surname").textValue()).isEqualTo("Kowalski");
        assertThat(trainer.get("images")).isNotNull();
        assertThat(trainer.get("images").get(0)).isNull();
        assertThat(trainer.get("description").get("synopsis")).isNotNull();
        assertThat(trainer.get("description").get("synopsis").textValue()).isEqualTo("Sample synopsis");
        assertThat(trainer.get("description").get("full")).isNotNull();
        assertThat(trainer.get("description").get("full").textValue()).isEqualTo("Sample full");
        assertThat(trainer.get("description").get("trainings")).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0)).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0).get("trainingId")).isNotNull();
        assertThat(trainer.get("description").get("trainings").get(0).get("title").textValue())
                .isEqualTo("Test training type");
        assertThat(trainer.get("images").isEmpty()).isTrue();

        testDatabase(1,1, 1, 0);
    }

    private HttpEntity<Object> getRequestEntityWithoutImage() {
        HttpHeaders headers = getHeaders();
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }
}
