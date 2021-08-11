package com.healthy.gym.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.auth.configuration.tests.TestCountry;
import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.pojo.request.CreateUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.auth.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.auth.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
public class UseControllerIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));

    @LocalServerPort
    private Integer port;
    @Autowired
    private TestRestTemplate restTemplate;
    private CreateUserRequest request;
    @Autowired
    private MongoTemplate mongoTemplate;
    @MockBean
    private JavaMailSender javaMailSender;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Nested
    class WhenCreateUser {
        @BeforeEach
        void setUp() {
            request = new CreateUserRequest();
            request.setName("Jan");
            request.setSurname("Kowalski");
            request.setEmail("xmr09697@zwoho.com");
            request.setPhoneNumber("+48 685 263 683");
            request.setPassword("test12345");
            request.setMatchingPassword("test12345");

            doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.dropCollection(UserDocument.class);
        }

        @Nested
        class ShouldAcceptUserRegistration {
            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenRequestHasEveryFieldValid(TestCountry country) throws URISyntaxException, JsonProcessingException {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/users");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());

                HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

                String expectedMessage = messages.get("user.sing-up.success");

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("success").asBoolean()).isTrue();
                assertThat(responseEntity.getBody().get("errors").isEmpty()).isTrue();
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenRequestHasEveryFieldValidWithoutPhone(TestCountry country) throws URISyntaxException, JsonProcessingException {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/users");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());

                request.setPhoneNumber(null);

                HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

                String expectedMessage = messages.get("user.sing-up.success");

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("success").asBoolean()).isTrue();
                assertThat(responseEntity.getBody().get("errors").isEmpty()).isTrue();
            }
        }

        @Nested
        class ShouldRejectUserRegistration {
            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenRequestHasEveryFieldInvalid(TestCountry country) throws URISyntaxException, JsonProcessingException {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/users");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());

                request.setName("G");
                request.setSurname("Z");
                request.setEmail("g.kowalskiwp.pl");
                request.setPhoneNumber("685 263 6831");
                request.setPassword("test123");
                request.setMatchingPassword("testtest1234");

                HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
                assertThat(responseEntity.getBody().get("message").textValue())
                        .isEqualTo(messages.get("user.sing-up.failure"));
                assertThat(responseEntity.getBody().get("success").asBoolean()).isFalse();
                assertThat(responseEntity.getBody().get("errors").get("name").textValue())
                        .isEqualTo(messages.get("field.name.failure"));
                assertThat(responseEntity.getBody().get("errors").get("surname").textValue())
                        .isEqualTo(messages.get("field.surname.failure"));
                assertThat(responseEntity.getBody().get("errors").get("email").textValue())
                        .isEqualTo(messages.get("field.email.failure"));
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenRequestHasSomeFieldInvalidEmpty(TestCountry country) throws URISyntaxException, JsonProcessingException {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/users");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());

                request.setName(null);
                request.setSurname(null);
                request.setEmail("xmr09697@zwoho.com");
                request.setPhoneNumber(null);
                request.setPassword("test12345");
                request.setMatchingPassword("testtest12345");

                HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
                assertThat(responseEntity.getBody().get("message").textValue())
                        .isEqualTo(messages.get("user.sing-up.failure"));
                assertThat(responseEntity.getBody().get("success").asBoolean()).isFalse();
                assertThat(responseEntity.getBody().get("errors").get("name").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("surname").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("email")).isNull();
                assertThat(responseEntity.getBody().get("errors").get("email")).isNull();
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void whenRequestHasEveryFieldInvalidEmpty(TestCountry country) throws URISyntaxException, JsonProcessingException {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/users");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());

                HttpEntity<Object> requestEntity = new HttpEntity<>(new CreateUserRequest(), headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
                assertThat(responseEntity.getBody().get("message").textValue())
                        .isEqualTo(messages.get("user.sing-up.failure"));
                assertThat(responseEntity.getBody().get("success").asBoolean()).isFalse();
                assertThat(responseEntity.getBody().get("errors").get("name").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("surname").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("email").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("password").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("matchingPassword").textValue())
                        .isEqualTo(messages.get("field.required"));

            }
        }
    }

    @Nested
    class WhenConfirmRegistration {
        private String activationToken;
        private URI uri;

        @BeforeEach
        void setUp() throws URISyntaxException {
            String userId = UUID.randomUUID().toString();
            UserDocument userDocument = new UserDocument(
                    "Jan",
                    "Kowalski",
                    "xmr09697@zwoho.com",
                    "+48 685 263 683",
                    bCryptPasswordEncoder.encode("test12345"),
                    userId
            );
            UserDocument saveUserDocument = mongoTemplate.save(userDocument);

            activationToken = UUID.randomUUID().toString();
            RegistrationTokenDocument registrationTokenDocument =
                    new RegistrationTokenDocument(activationToken, saveUserDocument);

            mongoTemplate.save(registrationTokenDocument);
        }

        @AfterEach
        void tearDown() {
            mongoTemplate.dropCollection(UserDocument.class);
            mongoTemplate.dropCollection(RegistrationTokenDocument.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldRejectConfirmationWhenProvidedTokenIsInvalid(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = new URI("http://localhost:" + port + "/users/confirmRegistration?token=" + UUID.randomUUID());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());

            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

            String expectedMessage = messages.get("registration.confirmation.token.invalid");
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Not Found");
            assertThat(responseEntity.getBody().get("status").numberValue()).isEqualTo(404);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();

        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldAcceptConfirmationWhenProvidedTokenIsValid(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            uri = new URI("http://localhost:" + port + "/users/confirmRegistration?token=" + activationToken);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());

            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
            assertThat(responseEntity.getBody().get("message").textValue())
                    .isEqualTo(messages.get("registration.confirmation.token.valid"));
            assertThat(responseEntity.getBody().get("success").asBoolean()).isTrue();
            assertThat(responseEntity.getBody().get("errors").isEmpty()).isTrue();
        }
    }
}
