package com.healthy.gym.account.controller.notification.integration.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.dto.UserNotificationDTO;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class MarkNotificationAsReadIntegrationTest {

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

    @Autowired
    private TestDocumentUtilComponent testUtil;

    private String userToken;
    private String userId;
    private String notificationId;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        UserDocument user = testUtil.saveAndGetTestUser(userId);

        var notificationDocument = testUtil
                .saveAndGetTestNotificationDocument(false, user);
        notificationId = notificationDocument.getNotificationId();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(NotificationDocument.class);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.POST, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(null, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", userToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getUri(String userId, String notificationId) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/notification/" + notificationId + "/user/" + userId);
    }

    @Test
    void shouldMarkAsRead() throws Exception {
        testDatabase(false);

        URI uri = getUri(userId, notificationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        ObjectMapper objectMapper = new ObjectMapper();
        UserNotificationDTO userNotificationDTO = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });

        assertThat(userNotificationDTO.getNotificationId()).isEqualTo(notificationId);
        assertThat(userNotificationDTO.isMarkAsRead()).isTrue();

        testDatabase(true);
    }

    private void testDatabase(boolean isMarkAsRead) {
        var notifications = mongoTemplate.findAll(NotificationDocument.class);
        assertThat(notifications).hasSize(1);
        var notification = notifications.get(0);
        assertThat(notification.isMarkAsRead()).isEqualTo(isMarkAsRead);
    }

    @Test
    void shouldThrowUserNotFoundException() throws Exception {
        testDatabase(false);
        mongoTemplate.dropCollection(UserDocument.class);

        URI uri = getUri(userId, notificationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.found.user.id");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        testDatabase(false);
    }

    @Test
    void shouldThrowNotificationNotFoundException() throws Exception {
        testDatabase(false);
        mongoTemplate.dropCollection(NotificationDocument.class);

        URI uri = getUri(userId, notificationId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.found.notification");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
