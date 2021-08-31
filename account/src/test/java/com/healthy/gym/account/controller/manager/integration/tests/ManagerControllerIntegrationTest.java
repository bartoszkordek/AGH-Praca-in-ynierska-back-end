package com.healthy.gym.account.controller.manager.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.pojo.request.ChangeUserRolesRequest;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class ManagerControllerIntegrationTest {

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

    private String adminToken;
    private String userId;
    private UserDocument user;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        adminToken = tokenFactory.getAdminToken();
        userId = UUID.randomUUID().toString();
        user = testUtil.saveAndGetTestUser(userId);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri, List<String> roles) throws JsonProcessingException {
        var changeRolesRequest = new ChangeUserRolesRequest();
        changeRolesRequest.setRoles(roles);
        var request = getAuthRequest(changeRolesRequest);
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.POST, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest(ChangeUserRolesRequest request) throws JsonProcessingException {
        var headers = getHeadersWithAuth();
        ObjectMapper objectMapper = new ObjectMapper();
        var requestStr = objectMapper.writeValueAsString(request);
        return new HttpEntity<>(requestStr, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", adminToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/manager/user/" + userId + "/roles");
    }

    @Test
    void shouldChangeUserRoles() throws Exception {
        testDatabase(GymRole.USER);

        URI uri = getUri(userId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri, List.of("user", "trainer", "employee"));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("user.roles.changed");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode roles = body.get("user").get("roles");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> roleStr = objectMapper
                .readValue(roles.toString(), new TypeReference<>() {
                });
        assertThat(roleStr).contains("USER", "TRAINER", "EMPLOYEE");

        testDatabase(GymRole.USER, GymRole.TRAINER, GymRole.EMPLOYEE);
    }

    private void testDatabase(GymRole... gymRoles) {
        var users = mongoTemplate.findAll(UserDocument.class);
        var user = users.get(0);
        assertThat(user.getGymRoles()).contains(gymRoles);
    }

    @Test
    void shouldAlwaysHaveUserRole() throws Exception {
        testDatabase(GymRole.USER);

        URI uri = getUri(userId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri, List.of("trainer", "employee"));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("user.roles.changed");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

        JsonNode roles = body.get("user").get("roles");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> roleStr = objectMapper
                .readValue(roles.toString(), new TypeReference<>() {
                });
        assertThat(roleStr).contains("USER", "TRAINER", "EMPLOYEE");

        testDatabase(GymRole.USER, GymRole.TRAINER, GymRole.EMPLOYEE);
    }

    @Test
    void shouldThrowUserNotFound() throws Exception {
        mongoTemplate.dropCollection(UserDocument.class);

        URI uri = getUri(userId);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri, List.of("user", "trainer", "employee"));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.not.found.user.id");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}