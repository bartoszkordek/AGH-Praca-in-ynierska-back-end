package com.healthy.gym.account.controller.accountController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.pojo.request.ChangeUserDataRequest;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenChangeUserDataIntegrationTest {

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userToken;
    private String adminToken;
    private String userId;
    private UserDocument janKowalski;
    private ChangeUserDataRequest request;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
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

        request = new ChangeUserDataRequest();

        mongoTemplate.save(janKowalski);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldAcceptRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserChangesItsOwnData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/changeUserData/" + userId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", userToken);

            request.setEmail("jan2.kowalski@test.com");
            request.setName("Janek");
            request.setSurname("Kowal");
            request.setPhoneNumber("+48 666 777 888");

            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            String expectedMessage = messages.get("account.change.user.data.success");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PATCH, requestEntity, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Janek");
            assertThat(responseEntity.getBody().get("surname").textValue()).isEqualTo("Kowal");
            assertThat(responseEntity.getBody().get("email").textValue()).isEqualTo("jan2.kowalski@test.com");
            assertThat(responseEntity.getBody().get("phone").textValue()).isEqualTo("+48 666 777 888");
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAdminChangesUserData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/changeUserData/" + userId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", adminToken);

            request.setEmail("jan2.kowalski@test.com");
            request.setName("Janek");

            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            String expectedMessage = messages.get("account.change.user.data.success");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PATCH, requestEntity, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("name").textValue()).isEqualTo("Janek");
            assertThat(responseEntity.getBody().get("surname").textValue()).isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get("email").textValue()).isEqualTo("jan2.kowalski@test.com");
            assertThat(responseEntity.getBody().get("phone").textValue()).isEqualTo("666 777 888");
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAdminChangesUserData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/changeUserData/" +UUID.randomUUID());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", adminToken);

            request.setEmail("jan2.kowalski@test.com");
            request.setName("Janek");

            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            String expectedMessage = messages.get("exception.account.not.found");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PATCH, requestEntity, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Not Found");
            assertThat(responseEntity.getBody().get("status").numberValue()).isEqualTo(404);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }
    }
}
