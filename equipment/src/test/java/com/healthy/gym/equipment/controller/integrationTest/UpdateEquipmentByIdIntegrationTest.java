package com.healthy.gym.equipment.controller.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.equipment.component.TokenManager;
import com.healthy.gym.equipment.configuration.TestCountry;
import com.healthy.gym.equipment.configuration.TestRoleTokenFactory;
import com.healthy.gym.equipment.data.document.EquipmentDocument;
import com.healthy.gym.equipment.data.document.ImageDocument;
import com.healthy.gym.equipment.data.document.TrainingTypeDocument;
import com.healthy.gym.equipment.model.request.EquipmentRequest;
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
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.equipment.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.equipment.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class UpdateEquipmentByIdIntegrationTest {

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

    private String trainingTypeId1;
    private String trainingTypeId2;
    private String equipmentId;
    private String adminToken;
    private Resource updatedTrainingTypeImageResource;
    private Resource updatedEquipmentImageResource;
    private EquipmentRequest equipmentRequest;
    private String expectedTrainingTypeUpdatedImageUrl;
    private String expectedTrainingTypeCurrentImageUrl;
    private String expectedEquipmentUpdatedImageUrl;
    private String expectedEquipmentCurrentImageUrl;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        trainingTypeId1 = UUID.randomUUID().toString();
        trainingTypeId2 = UUID.randomUUID().toString();

        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);

        Resource trainingTypeImageResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");
        updatedTrainingTypeImageResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        String trainingTypeImageId = UUID.randomUUID().toString();
        ImageDocument trainingTypeImageDocument = new ImageDocument(
                trainingTypeImageId,
                new Binary(getImageBytes(trainingTypeImageResource)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument trainingTypeSavedImageDocument = mongoTemplate.save(trainingTypeImageDocument);

        ImageDocument updatedTrainingTypeImageDocument = new ImageDocument(
                trainingTypeImageId,
                new Binary(getImageBytes(trainingTypeImageResource)),
                MediaType.IMAGE_JPEG_VALUE
        );

        ImageDocument updatedTrainingTypeSavedImageDocument = mongoTemplate.save(updatedTrainingTypeImageDocument);

        TrainingTypeDocument trainingTypeDocument1 = new TrainingTypeDocument(
                trainingTypeId1,
                "Test name",
                "Test description",
                LocalTime.of(2, 30),
                trainingTypeSavedImageDocument
        );

        mongoTemplate.save(trainingTypeDocument1);

        TrainingTypeDocument trainingTypeDocument2 = new TrainingTypeDocument(
                trainingTypeId2,
                "Test name2",
                "Test description2",
                LocalTime.of(2, 30),
                updatedTrainingTypeSavedImageDocument

        );

        mongoTemplate.save(trainingTypeDocument2);


        Resource imageEquipmentResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        String imageEquipmentId = UUID.randomUUID().toString();
        ImageDocument imageEquipmentDocumentEquipment = new ImageDocument(
                UUID.randomUUID().toString(),
                new Binary(getImageBytes(imageEquipmentResource)),
                MediaType.IMAGE_JPEG_VALUE
        );
        ImageDocument savedImageDocumentEquipment = mongoTemplate.save(imageEquipmentDocumentEquipment);

        updatedEquipmentImageResource = new ClassPathResource("testImages/2017-lexus-lc-500.jpg");

        equipmentId = UUID.randomUUID().toString();
        EquipmentDocument equipmentDocument = new EquipmentDocument(
                equipmentId,
                "Test equipment title 1",
                List.of(savedImageDocumentEquipment),
                List.of("http://localhost:8020/equipment/" + equipmentId + "?version=test1"),
                "Test synopsis 1",
                List.of(trainingTypeDocument1)
        );

        mongoTemplate.save(equipmentDocument);

        String digestTrainingTypeUpdated = DigestUtils.md5DigestAsHex(getImageBytes(updatedTrainingTypeImageResource));
        String digestTrainingTypeCurrent = DigestUtils.md5DigestAsHex(getImageBytes(trainingTypeImageResource));
        expectedTrainingTypeUpdatedImageUrl = "http://localhost:8020/trainings/trainingType/image/"
                + trainingTypeImageId + "?version=" + digestTrainingTypeUpdated;
        expectedTrainingTypeCurrentImageUrl = "http://localhost:8020/trainings/trainingType/image/"
                + trainingTypeImageId + "?version=" + digestTrainingTypeCurrent;

        String digestEquipmentUpdated = DigestUtils.md5DigestAsHex(getImageBytes(updatedEquipmentImageResource));
        String digestEquipmentCurrent = DigestUtils.md5DigestAsHex(getImageBytes(imageEquipmentResource));
        expectedEquipmentUpdatedImageUrl = "http://localhost:8020/equipment/image/"
                + imageEquipmentId + "?version=" + digestEquipmentUpdated;
        expectedEquipmentCurrentImageUrl = "http://localhost:8020/equipment/image/"
                + imageEquipmentId + "?version=" + digestEquipmentCurrent;

        equipmentRequest = new EquipmentRequest();
        equipmentRequest.setTitle("Updated equipment title");
        equipmentRequest.setSynopsis("Updated synopsis");
        equipmentRequest.setTrainingIds(List.of(trainingTypeId2));
    }

    private byte[] getImageBytes(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        return inputStream.readAllBytes();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(ImageDocument.class);
        mongoTemplate.dropCollection(EquipmentDocument.class);
    }

    private HttpEntity<Object> getRequestEntity(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("image", getImagePart());
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }

    private HttpEntity<Object> getRequestEntityWithoutImage(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }

    private HttpEntity<Object> getImagePart() {
        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new HttpEntity<>(updatedEquipmentImageResource, imageHeaders);
    }

    private HttpEntity<Object> getBodyPart() {
        HttpHeaders bodyHeaders = new HttpHeaders();
        bodyHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(equipmentRequest, bodyHeaders);
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    @Nested
    class ShouldAcceptRequestAndShouldUpdateProperEquipment {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusOK(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            System.out.println(responseEntity.getBody());
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
        void shouldReturnExpectedMessage(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            String expectedMessage = messages.get("equipment.updated");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNoErrors(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("errors")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotNullEquipmentId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("equipmentId").textValue()).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperTitle(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("title").textValue()).isEqualTo("Updated equipment title");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageData(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("images").isEmpty())
                    .isFalse();
            assertThat(responseEntity.getBody().get("equipment").get("images").get(0))
                    .isNotNull();
        }

        @Nested
        class ShouldReturnDescription{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldReturnSynopsis(TestCountry country) throws URISyntaxException {
                ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                assertThat(responseEntity.getBody().get("equipment").get("description").get("synopsis").textValue())
                        .isEqualTo("Updated synopsis");
            }

            @Nested
            class ShouldReturnTraining{

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldReturnTrainingId(TestCountry country) throws URISyntaxException {
                    ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                    assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0).get("trainingId").textValue())
                            .isNotNull();
                }

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldReturnTrainingTitle(TestCountry country) throws URISyntaxException {
                    ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                    assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0).get("title").textValue())
                            .isEqualTo("Test name2");
                }
            }
        }


        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/" + equipmentId);
            return restTemplate.exchange(
                    uri,
                    HttpMethod.PUT,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class shouldAcceptRequestAndShouldUpdateProperEquipmentWithoutImage {

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
        void shouldReturnExpectedMessage(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            String expectedMessage = messages.get("equipment.updated");
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNoErrors(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("errors")).isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotNullEquipmentId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("equipmentId").textValue()).isNotNull();
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperTitle(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("title").textValue()).isEqualTo("Updated equipment title");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperEmptyImageData(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("equipment").get("images").isEmpty())
                    .isTrue();
        }

        @Nested
        class ShouldReturnDescription{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldReturnSynopsis(TestCountry country) throws URISyntaxException {
                ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                assertThat(responseEntity.getBody().get("equipment").get("description").get("synopsis").textValue())
                        .isEqualTo("Updated synopsis");
            }

            @Nested
            class ShouldReturnTraining{

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldReturnTrainingId(TestCountry country) throws URISyntaxException {
                    ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                    assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0).get("trainingId").textValue())
                            .isNotNull();
                }

                @ParameterizedTest
                @EnumSource(TestCountry.class)
                void shouldReturnTrainingTitle(TestCountry country) throws URISyntaxException {
                    ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
                    assertThat(responseEntity.getBody().get("equipment").get("description").get("trainings").get(0).get("title").textValue())
                            .isEqualTo("Test name2");
                }
            }
        }


        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            URI uri = new URI("http://localhost:" + port + "/" + equipmentId);
            return restTemplate.exchange(
                    uri,
                    HttpMethod.PUT,
                    getRequestEntityWithoutImage(testedLocale),
                    JsonNode.class
            );
        }
    }
}
