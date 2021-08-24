package com.healthy.gym.trainings.controller.training.type.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.ImageDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenCreateTrainingTypeIntegrationTest {

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
    private TestRoleTokenFactory tokenFactory;

    private String adminToken;
    private TrainingTypeRequest trainingTypeRequest;
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

        trainingTypeRequest = new TrainingTypeRequest();
        trainingTypeRequest.setName("Test name");
        trainingTypeRequest.setDescription("Test description");
        trainingTypeRequest.setDuration("02:30:00.000");

        imageResource = new ClassPathResource("testImages/shiba_inu_smile_1.jpg");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
    }

    @Test
    void ShouldReturnNewTrainingTypeWithImageUrl() throws URISyntaxException, IOException {
        testDatabase(0, 0);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getURI(),
                HttpMethod.POST,
                getRequestEntity(),
                JsonNode.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        TestCountry country = TestCountry.ENGLAND;
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("training.type.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();

        JsonNode trainingType = body.get("trainingType");
        assertThat(trainingType.get("trainingTypeId").textValue()).isNotNull();
        assertThat(trainingType.get("name").textValue()).isEqualTo("Test name");
        assertThat(trainingType.get("description").textValue()).isEqualTo("Test description");
        assertThat(trainingType.get("duration").textValue()).isEqualTo("02:30:00");

        String image = trainingType.get("image").textValue();
        assertThat(image).startsWith("http://localhost:8020/trainings/trainingType/image/");
        byte[] imageBytes = imageResource.getInputStream().readAllBytes();
        assertThat(image).endsWith("?version=" + DigestUtils.md5DigestAsHex(imageBytes));

        testDatabase(1, 1);
    }

    private void testDatabase(int expectedNumberOfTrainingTypes, int expectedNumberOfImages) {
        var trainings = mongoTemplate.findAll(TrainingTypeDocument.class);
        var images = mongoTemplate.findAll(ImageDocument.class);

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
        return new HttpEntity<>(trainingTypeRequest, bodyHeaders);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private URI getURI() throws URISyntaxException {
        return new URI("http://localhost:" + port + "/trainingType");
    }

    @Test
    void ShouldReturnNewTrainingTypeWithoutImage() throws URISyntaxException {
        testDatabase(0, 0);

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                getURI(),
                HttpMethod.POST,
                getRequestEntityWithoutImage(),
                JsonNode.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        TestCountry country = TestCountry.ENGLAND;
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("training.type.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();

        JsonNode trainingType = body.get("trainingType");
        assertThat(trainingType.get("trainingTypeId").textValue()).isNotNull();
        assertThat(trainingType.get("name").textValue()).isEqualTo("Test name");
        assertThat(trainingType.get("description").textValue()).isEqualTo("Test description");
        assertThat(trainingType.get("duration").textValue()).isEqualTo("02:30:00");
        assertThat(trainingType.get("image")).isNull();

        testDatabase(1, 0);
    }

    private HttpEntity<Object> getRequestEntityWithoutImage() {
        HttpHeaders headers = getHeaders();
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }
}
