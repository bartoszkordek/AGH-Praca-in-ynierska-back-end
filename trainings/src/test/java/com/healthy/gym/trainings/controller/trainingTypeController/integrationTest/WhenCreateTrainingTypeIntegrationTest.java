package com.healthy.gym.trainings.controller.trainingTypeController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenCreateTrainingTypeIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
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
    }

    @BeforeEach
    void setUp() throws IOException {
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

    private HttpEntity<Object> getRequestEntity(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
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

    private String getExpectedImageBase64() throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] imageBytes = inputStream.readAllBytes();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private HttpEntity<Object> getRequestEntityWithoutImage(Locale testedLocale) {
        HttpHeaders headers = getHeaders(testedLocale);
        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("body", getBodyPart());
        return new HttpEntity<>(multipartRequest, headers);
    }

    private HttpHeaders getHeaders(Locale testedLocale) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private URI getURI() throws URISyntaxException {
        return new URI("http://localhost:" + port + "/trainingType");
    }

    @Nested
    class shouldAcceptRequestAndShouldReturnNewTrainingTypeWithoutImage {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusCreated(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
            String expectedMessage = messages.get("training.type.created");
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
        void shouldReturnNotNullTrainingTypeId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("trainingTypeId").textValue()).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperName(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Test name");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("description").textValue()).isEqualTo("Test description");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDuration(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("duration").textValue()).isEqualTo("02:30:00.000");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnImageAsNull(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("image")).isNull();
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            return restTemplate.exchange(
                    getURI(),
                    HttpMethod.POST,
                    getRequestEntityWithoutImage(testedLocale),
                    JsonNode.class
            );
        }
    }

    @Nested
    class ShouldAcceptRequestAndShouldReturnNewTrainingType {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnResponseWithStatusCreated(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
            String expectedMessage = messages.get("training.type.created");
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
        void shouldReturnNotNullTrainingTypeId(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("trainingTypeId").textValue()).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperName(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Test name");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDescription(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("description").textValue()).isEqualTo("Test description");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperDuration(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("duration").textValue()).isEqualTo("02:30:00.000");
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageData(TestCountry country) throws URISyntaxException, IOException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("image").get("data").textValue())
                    .isEqualTo(getExpectedImageBase64());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnProperImageFormat(TestCountry country) throws URISyntaxException {
            ResponseEntity<JsonNode> responseEntity = getResponseEntity(country);
            assertThat(responseEntity.getBody().get("image").get("format").textValue()).isEqualTo("image/jpeg");
        }

        private ResponseEntity<JsonNode> getResponseEntity(TestCountry country) throws URISyntaxException {
            Locale testedLocale = convertEnumToLocale(country);
            return restTemplate.exchange(
                    getURI(),
                    HttpMethod.POST,
                    getRequestEntity(testedLocale),
                    JsonNode.class
            );
        }
    }


}
