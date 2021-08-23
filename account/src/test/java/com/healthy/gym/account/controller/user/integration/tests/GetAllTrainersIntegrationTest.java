package com.healthy.gym.account.controller.user.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetAllTrainersIntegrationTest {
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
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestDocumentUtilComponent utilComponent;
    @Autowired
    private TestRoleTokenFactory tokenFactory;

    private String adminToken;
    private byte[] imageBytes;
    private String digest;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        adminToken = tokenFactory.getAdminToken();

        for (int i = 0; i < 5; i++) {
            utilComponent.saveAndGetTestUser();
        }

        for (int i = 0; i < 2; i++) {
            utilComponent.saveAndGetTestTrainer();
        }
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Test
    void shouldReturnOnlyTrainers() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/admin/trainers");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", adminToken);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(2);

    }


    @Test
    void shouldReturnAllUsers() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/admin/users");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", adminToken);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(7);

    }

    @Test
    void shouldReturnExceptionMessage() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/admin/users");
        mongoTemplate.dropCollection(UserDocument.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", adminToken);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.NOT_FOUND);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.no.user.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
