package com.healthy.gym.equipment.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
import com.healthy.gym.equipment.component.TokenManager;
import com.healthy.gym.equipment.configuration.TestCountry;
import com.healthy.gym.equipment.configuration.TestRoleTokenFactory;
import com.healthy.gym.equipment.data.document.ImageDocument;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.equipment.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class CreateEquipmentIntegrationTest {

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
    private EquipmentRequest equipmentRequest;
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

        equipmentRequest = new EquipmentRequest();
        equipmentRequest.setTitle("Test title");
        equipmentRequest.setTrainingIds(List.of(trainingTypeId));
        equipmentRequest.setSynopsis("Test synopsis");

        imageResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(EquipmentDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(ImageDocument.class);
    }

    @Test
    void shouldReturnNewEquipmentWithImageUrl() throws URISyntaxException, IOException {
        testDatabase(0, 1, 0);

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
        String expectedMessage = messages.get("equipment.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();
        JsonNode equipment = body.get("equipment");
        assertThat(equipment.get("equipmentId").textValue()).isNotNull();
        assertThat(equipment.get("title").textValue()).isEqualTo("Test title");
        assertThat(equipment.get("description")).isNotNull();
        assertThat(equipment.get("description").get("synopsis")).isNotNull();
        assertThat(equipment.get("description").get("synopsis").textValue()).isEqualTo("Test synopsis");
        assertThat(equipment.get("description").get("trainings")).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0)).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0).get("trainingId")).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0).get("title").textValue())
                .isEqualTo("Test training type");
        assertThat(equipment.get("images").isEmpty()).isFalse();

        String image = equipment.get("images").get(0).textValue();
        assertThat(image).startsWith("http://localhost:8020/equipment/image/");
        byte[] imageBytes = imageResource.getInputStream().readAllBytes();
        assertThat(image).endsWith("?version=" + DigestUtils.md5DigestAsHex(imageBytes));

        testDatabase(1,1, 1);
    }

    private void testDatabase(int expectedNumberOfEquipments, int expectedNumberOfTrainingTypes, int expectedNumberOfImages) {
        var equipment = mongoTemplate.findAll(EquipmentDocument.class);
        var trainings = mongoTemplate.findAll(TrainingTypeDocument.class);
        var images = mongoTemplate.findAll(ImageDocument.class);

        assertThat(equipment.size()).isEqualTo(expectedNumberOfEquipments);
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
        return new HttpEntity<>(equipmentRequest, bodyHeaders);
    }


    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private URI getURI() throws URISyntaxException {
        return new URI("http://localhost:" + port );
    }

    @Test
    void shouldReturnNewEquipmentWithoutImage() throws URISyntaxException {
        testDatabase(0, 1,0);

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
        String expectedMessage = messages.get("equipment.created");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(body.get("errors")).isNull();
        JsonNode equipment = body.get("equipment");
        assertThat(equipment.get("equipmentId").textValue()).isNotNull();
        assertThat(equipment.get("title").textValue()).isEqualTo("Test title");
        assertThat(equipment.get("description")).isNotNull();
        assertThat(equipment.get("description").get("synopsis")).isNotNull();
        assertThat(equipment.get("description").get("synopsis").textValue()).isEqualTo("Test synopsis");
        assertThat(equipment.get("description").get("trainings")).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0)).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0).get("trainingId")).isNotNull();
        assertThat(equipment.get("description").get("trainings").get(0).get("title").textValue())
                .isEqualTo("Test training type");
        assertThat(equipment.get("images").isEmpty()).isTrue();

        testDatabase(1, 1, 0);
    }

    private HttpEntity<Object> getRequestEntityWithoutImage() {
        HttpHeaders headers = getHeaders();
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }
}
