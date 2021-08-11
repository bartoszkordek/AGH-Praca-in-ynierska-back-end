package com.healthy.gym.account.controller.accountController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.pojo.request.ChangeUserDataRequest;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
class WhenChangeUserDataIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
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
    private ChangeUserDataRequest request;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();

        userToken = tokenFactory.getUserToken(userId);
        adminToken = tokenFactory.getAdminToken(adminId);

        UserDocument janKowalski = new UserDocument("Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                userId
        );

        UserDocument krzysztofNowak = new UserDocument(
                "Krzysztof",
                "Nowak",
                "krzysztof.nowak@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString()
        );

        request = new ChangeUserDataRequest();

        mongoTemplate.save(janKowalski);
        mongoTemplate.save(krzysztofNowak);
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

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldAcceptRequestAndShouldThrowExceptionWhenEmailAlreadyExists(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/changeUserData/" + userId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", adminToken);
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            request.setEmail("krzysztof.nowak@test.com");
            request.setName("Krzysztof");

            HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
            String expectedMessage = messages.get("exception.email.occupied");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PATCH, requestEntity, JsonNode.class);

            JsonNode responseBody = responseEntity.getBody();

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
            assertThat(responseBody.get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseBody.get("error").textValue()).isEqualTo("Conflict");
            assertThat(responseBody.get("status").numberValue()).isEqualTo(409);
            assertThat(responseBody.get("timestamp")).isNotNull();
        }
    }

    @Nested
    class ShouldRejectRequest {
        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenAdminChangesUserData(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/changeUserData/" + UUID.randomUUID());

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
