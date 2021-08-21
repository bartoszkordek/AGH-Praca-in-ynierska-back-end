package com.healthy.gym.account.controller.accountController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.data.document.UserDocument;
import io.jsonwebtoken.Jwts;
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
import java.util.*;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenDeleteAccountIntegrationTest {

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userToken;
    private String adminToken;
    private String userId;
    private UserDocument janKowalski;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    private void cleanUp() {
        mongoTemplate.remove(janKowalski);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();

        userToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(userId)
                .claim("roles", List.of("ROLE_USER"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();

        adminToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(adminId)
                .claim("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();

        janKowalski = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                userId
        );

        mongoTemplate.save(janKowalski);
    }

    @Nested
    class ShouldAcceptRequest {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserDeletesOwnAccount(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + userId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("delete.account.success");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAdminDeletesUserAccount(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + userId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", adminToken);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("delete.account.success");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class ShouldReject {

        @AfterEach
        void tearDown() {
            cleanUp();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectRequestWhenIdDoesNotMatchUserIdInToken(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + UUID.randomUUID());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("exception.access.denied");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
            assertThat(responseEntity.getBody().get("status").numberValue()).isEqualTo(403);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectRequestWhenUserNotFound(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/" + UUID.randomUUID());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", adminToken);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("exception.account.not.found");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Not Found");
            assertThat(responseEntity.getBody().get("status").numberValue()).isEqualTo(404);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }
    }

}
