package com.healthy.gym.trainings.controller.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.model.request.LocationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
class LocationControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    private String managerToken;
    private String requestContent;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        managerToken = tokenFactory.getManagerToken(UUID.randomUUID().toString());

        ObjectMapper objectMapper = new ObjectMapper();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setName("Sala nr 1");

        requestContent = objectMapper.writeValueAsString(locationRequest);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(LocationDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldCreateNewLocation(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("location.created");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("location").get("name").textValue()).isEqualTo("Sala nr 1");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        List<LocationDocument> locationDocumentList = mongoTemplate.findAll(LocationDocument.class);
        assertThat(locationDocumentList.size()).isEqualTo(1);
        assertThat(locationDocumentList.get(0).getName()).isEqualTo("Sala nr 1");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowDuplicatedLocationWhenCreateNewLocation(TestCountry country) throws Exception {
        mongoTemplate.save(new LocationDocument(UUID.randomUUID().toString(), "Sala nr 1"));

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("exception.duplicated.location.name");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnLocationList(TestCountry country) throws Exception {
        String locationID1 = UUID.randomUUID().toString();
        String locationID2 = UUID.randomUUID().toString();

        mongoTemplate.save(new LocationDocument(locationID1, "Sala nr 1"));
        mongoTemplate.save(new LocationDocument(locationID2, "Sala nr 2"));

        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().isArray()).isTrue();
        assertThat(responseEntity.getBody().get(0).get("id").textValue()).isEqualTo(locationID1);
        assertThat(responseEntity.getBody().get(0).get("name").textValue()).isEqualTo("Sala nr 1");
        assertThat(responseEntity.getBody().get(1).get("id").textValue()).isEqualTo(locationID2);
        assertThat(responseEntity.getBody().get(1).get("name").textValue()).isEqualTo("Sala nr 2");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldUpdateLocationName(TestCountry country) throws Exception {
        String locationID1 = UUID.randomUUID().toString();
        mongoTemplate.save(new LocationDocument(locationID1, "Sala nr A"));

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location/" + locationID1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
        String expectedMessage = messages.get("location.updated");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("location").get("name").textValue()).isEqualTo("Sala nr 1");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        List<LocationDocument> locationDocumentList = mongoTemplate.findAll(LocationDocument.class);
        assertThat(locationDocumentList.size()).isEqualTo(1);
        assertThat(locationDocumentList.get(0).getName()).isEqualTo("Sala nr 1");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldDeleteLocation(TestCountry country) throws Exception {
        String locationID1 = UUID.randomUUID().toString();
        mongoTemplate.save(new LocationDocument(locationID1, "Sala nr 1"));

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location/" + locationID1);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

        String expectedMessage = messages.get("location.removed");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("location").get("id").textValue()).isEqualTo(locationID1);
        assertThat(responseEntity.getBody().get("location").get("name").textValue()).isEqualTo("Sala nr 1");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        List<LocationDocument> locationDocumentList = mongoTemplate.findAll(LocationDocument.class);
        assertThat(locationDocumentList.size()).isZero();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowErrorWhenDeleteLocation(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/location/" + UUID.randomUUID());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

        String expectedMessage = messages.get("exception.location.not.found");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}
