package com.healthy.gym.account.controller.privacyController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.document.UserPrivacyDocument;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenGetUserPrivacyIntegrationTest {
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userToken;
    private String adminToken;
    private String userId;

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
        adminToken = tokenFactory.getAdminToken();

        UserDocument janKowalski = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                userId
        );

        janKowalski = mongoTemplate.save(janKowalski);

        UserPrivacyDocument privacyDocument = new UserPrivacyDocument(
                true,
                false,
                true,
                false,
                janKowalski
        );

        mongoTemplate.save(privacyDocument);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestWhenUserChangesItsOwnData(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + userId + "/privacy");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        assertThat(body.get("message")).isNull();
        assertThat(body.get("regulationsAccepted").asBoolean()).isTrue();
        assertThat(body.get("allowShowingTrainingsParticipation").asBoolean()).isFalse();
        assertThat(body.get("allowShowingUserStatistics").asBoolean()).isTrue();
        assertThat(body.get("allowShowingAvatar").asBoolean()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowErrorWhenUserIsNotFound(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/" + UUID.randomUUID() + "/privacy");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", adminToken);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        String expectedMessage = messages.get("exception.account.not.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
