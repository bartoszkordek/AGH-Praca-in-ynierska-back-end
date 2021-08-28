package com.healthy.gym.equipment.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.equipment.component.TokenManager;
import com.healthy.gym.equipment.configuration.TestCountry;
import com.healthy.gym.equipment.configuration.TestRoleTokenFactory;
import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.ImageDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
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

import static com.healthy.gym.equipment.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.equipment.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class RemoveEquipmentByIdIntegrationTest {

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

    private String trainingTypeId;
    private String equipmentId;
    private Resource imageResource;
    private String adminToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        trainingTypeId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        imageResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        ImageDocument imageDocument = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResource)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument savedImageDocument = mongoTemplate.save(imageDocument);

        TrainingTypeDocument trainingTypeDocument = new TrainingTypeDocument(
                trainingTypeId,
                "Test name",
                "Test description",
                LocalTime.of(2, 30),
                savedImageDocument
        );

        mongoTemplate.save(trainingTypeDocument);

        Resource imageResourceEquipment1 = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        ImageDocument imageDocumentEquipment1 = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageResourceEquipment1)),
                MediaType.IMAGE_JPEG_VALUE
        );
        ImageDocument savedImageDocumentEquipment = mongoTemplate.save(imageDocumentEquipment1);

        equipmentId = UUID.randomUUID().toString();
        EquipmentDocument equipmentDocument = new EquipmentDocument(
                equipmentId,
                "Test equipment title 1",
                List.of(savedImageDocumentEquipment),
                List.of("http://localhost:8020/equipment/" + equipmentId + "?version=test1"),
                "Test synopsis 1",
                List.of(trainingTypeDocument)
        );

        mongoTemplate.save(equipmentDocument);
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
        mongoTemplate.dropCollection(EquipmentDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRemoveEquipmentDocument(TestCountry country) throws URISyntaxException, IOException {
        List<EquipmentDocument> equipmentDocumentList = mongoTemplate.findAll(EquipmentDocument.class);
        assertThat(equipmentDocumentList.size()).isEqualTo(1);

        List<ImageDocument> imageDocumentList = mongoTemplate.findAll(ImageDocument.class);
        assertThat(imageDocumentList.size()).isEqualTo(2);

        Locale testedLocale = convertEnumToLocale(country);
        URI uri = new URI("http://localhost:" + port + "/" + equipmentId);
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                getRequestEntity(testedLocale),
                JsonNode.class
        );

        System.out.println(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("equipment.removed");
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("errors")).isNull();
        assertThat(responseEntity.getBody().get("equipment").get("equipmentId").textValue()).isEqualTo(equipmentId);
        assertThat(responseEntity.getBody().get("equipment").get("title").textValue()).isEqualTo("Test equipment title 1");
        assertThat(responseEntity.getBody().get("equipment").get("images").get(0)).isNotNull();
        assertThat(responseEntity.getBody().get("equipment").get("description").get("synopsis").textValue())
                .isEqualTo("Test synopsis 1");
        assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0)
                .get("trainingId").textValue()).isEqualTo(trainingTypeId);
        assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0)
                .get("title").textValue()).isEqualTo("Test name");

        equipmentDocumentList = mongoTemplate.findAll(EquipmentDocument.class);
        assertThat(equipmentDocumentList.size()).isZero();
        imageDocumentList = mongoTemplate.findAll(ImageDocument.class);
        assertThat(imageDocumentList.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhileRemovingNonExistingEquipment(TestCountry country) throws URISyntaxException {
        Locale testedLocale = convertEnumToLocale(country);
        URI uri = new URI("http://localhost:" + port + "/" + UUID.randomUUID());
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                getRequestEntity(testedLocale),
                JsonNode.class
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("exception.not.found.equipment");
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
    }

    @Nested
    class ShouldNotDeleteEquipmentWhenNotAuthorized{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotDeleteEquipmentWhenNoToken(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/"+equipmentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);


            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotDeleteEquipmentWhenLoggedAsUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userId = UUID.randomUUID().toString();
            String userToken = tokenFactory.getUserToken(userId);

            URI uri = new URI("http://localhost:" + port + "/"+equipmentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            String expectedMessage = messages.get("exception.access.denied");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
        return headers;
    }

    private HttpEntity<Object> getRequestEntity(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        return new HttpEntity<>(null, headers);
    }

    private String getExpectedImageBase64() throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] imageBytes = inputStream.readAllBytes();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(imageBytes);
    }
}
