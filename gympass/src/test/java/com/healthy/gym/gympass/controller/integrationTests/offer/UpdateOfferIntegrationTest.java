package com.healthy.gym.gympass.controller.integrationTests.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.util.*;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
@Tag("integration")
class UpdateOfferIntegrationTest {

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

    @LocalServerPort
    private Integer port;
    private String userToken;
    private String managerToken;
    private String existingDocumentId;
    private String requestContent;
    private String invalidTitleRequestContent;
    private String invalidSubheaderRequestContent;
    private String invalidPeriodRequestContent;
    private String invalidSynopsisRequestContent;
    private String invalidFeaturesRequestContent;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {

        userToken = tokenFactory.getUserToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        existingDocumentId = UUID.randomUUID().toString();
        mongoTemplate.save(new GymPassDocument(
                existingDocumentId,
                "Pojedyncze wejście",
                "Zapraszamy jeżeli chcesz sprawdzić jak wygląda nasza siłownia",
                new Price(19.99, "zł", "jednorazowy"),
                false,
                new Description("Karnet uprawniający do jednorazowego skorzystania w pełni z usług ośrodka",
                        List.of("sauna", "siłownia", "basen"))
        ));

        ObjectMapper objectMapper = new ObjectMapper();

        GymPassOfferRequest gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle("Karnet miesięczny");
        gymPassOfferRequest.setSubheader("Najlepszy wybór dla osób aktywnych");
        gymPassOfferRequest.setAmount(139.99);
        gymPassOfferRequest.setCurrency("zł");
        gymPassOfferRequest.setPeriod("miesiąc");
        gymPassOfferRequest.setPremium(false);
        gymPassOfferRequest.setSynopsis("Karnet uprawniający do korzystania w pełni z usług ośrodka");
        gymPassOfferRequest.setFeatures(List.of("Full pakiet", "sauna", "siłownia", "basen"));

        requestContent = objectMapper.writeValueAsString(gymPassOfferRequest);

        GymPassOfferRequest invalidTitleGymPassOfferRequest = new GymPassOfferRequest();
        invalidTitleGymPassOfferRequest.setTitle("T");

        invalidTitleRequestContent = objectMapper.writeValueAsString(invalidTitleGymPassOfferRequest);

        GymPassOfferRequest invalidSubheaderGymPassOfferRequest = new GymPassOfferRequest();
        invalidSubheaderGymPassOfferRequest.setSubheader("S");

        invalidSubheaderRequestContent = objectMapper.writeValueAsString(invalidSubheaderGymPassOfferRequest);

        GymPassOfferRequest invalidPeriodGymPassOfferRequest = new GymPassOfferRequest();
        invalidPeriodGymPassOfferRequest.setPeriod("P");

        invalidPeriodRequestContent = objectMapper.writeValueAsString(invalidPeriodGymPassOfferRequest);

        GymPassOfferRequest invalidSynopsisGymPassOfferRequest = new GymPassOfferRequest();
        invalidSynopsisGymPassOfferRequest.setSynopsis("S");

        invalidSynopsisRequestContent = objectMapper.writeValueAsString(invalidSynopsisGymPassOfferRequest);

        List<String> features = new ArrayList<>();
        for (int i = 0; i < 21; i++)
            features.add("element " + i + 1);

        GymPassOfferRequest invalidFeaturesGymPassOfferRequest = new GymPassOfferRequest();
        invalidFeaturesGymPassOfferRequest.setFeatures(features);

        invalidFeaturesRequestContent = objectMapper.writeValueAsString(invalidFeaturesGymPassOfferRequest);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GymPassDocument.class);
    }

    @Nested
    class ShouldUpdateOffer {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldUpdateGymPassOfferWhenValidIdAndRequestBody(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("offer.updated");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("gymPass").get("title").textValue()).isEqualTo("Karnet miesięczny");
            assertThat(responseEntity.getBody().get("gymPass").get("subheader").textValue())
                    .isEqualTo("Najlepszy wybór dla osób aktywnych");
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("amount").asDouble()).isEqualTo(139.99);
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("currency").textValue()).isEqualTo("zł");
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("period").textValue()).isEqualTo("miesiąc");
            assertThat(responseEntity.getBody().get("gymPass").get("isPremium").booleanValue()).isFalse();
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("synopsis").textValue())
                    .isEqualTo("Karnet uprawniający do korzystania w pełni z usług ośrodka");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(0).textValue())
                    .isEqualTo("Full pakiet");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(1).textValue())
                    .isEqualTo("sauna");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(2).textValue())
                    .isEqualTo("siłownia");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(3).textValue())
                    .isEqualTo("basen");
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

            List<GymPassDocument> gymPassDocumentList = mongoTemplate.findAll(GymPassDocument.class);
            assertThat(gymPassDocumentList.size()).isEqualTo(1);
        }
    }

    @Nested
    class ShouldNotUpdateOffer {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowDuplicatedGymPassDocumentsWhenUpdateExistingOffer(TestCountry country) throws Exception {
            mongoTemplate.save(new GymPassDocument(
                    UUID.randomUUID().toString(),
                    "Karnet miesięczny",
                    "Najlepszy wybór dla osób aktywnych",
                    new Price(140.00, "zł", "miesiąc"),
                    false,
                    new Description("Karnet uprawniający do korzystania w pełni z usług ośrodka",
                            List.of("Full pakiet", "sauna", "siłownia", "basen"))
            ));

            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("exception.duplicated.offers");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message")
                    .textValue())).isEqualTo(expectedMessage);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInvalidGymPassOfferIdExceptionWhenInvalidId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidDocumentId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/offer/" + invalidDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("exception.invalid.offer.id");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @Nested
        class ShouldNotCreateOfferWhenNotAuthorized {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetOffersWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);


                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetOffersWhenLoggedAsUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                String expectedMessage = messages.get("exception.access.denied");

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }
        }

        @Nested
        class ShouldThrowBindException {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindExceptionWhenInvalidTitle(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);


                HttpEntity<Object> request = new HttpEntity<>(invalidTitleRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("errors").get("title").textValue())
                        .isEqualTo(messages.get("field.name.failure"));
                assertThat(responseEntity.getBody().get("errors").get("period").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindExceptionWhenInvalidSubheader(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(invalidSubheaderRequestContent, headers);
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

                JsonNode body = responseEntity.getBody();
                assert body != null;

                Map<String, String> messages = getMessagesAccordingToLocale(country);
                String expectedMessage = messages.get("request.bind.exception");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.required"));
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindExceptionWhenInvalidPeriod(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);


                HttpEntity<Object> request = new HttpEntity<>(invalidPeriodRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("errors").get("title").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("period").textValue())
                        .isEqualTo(messages.get("field.period.failure"));
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindExceptionWhenInvalidSynopsis(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);


                HttpEntity<Object> request = new HttpEntity<>(invalidSynopsisRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("errors").get("title").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("period").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("synopsis").textValue())
                        .isEqualTo(messages.get("field.synopsis.failure"));
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldThrowBindExceptionWhenInvalidFeatures(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/" + existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);


                HttpEntity<Object> request = new HttpEntity<>(invalidFeaturesRequestContent, headers);
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("errors").get("title").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("period").textValue())
                        .isEqualTo(messages.get("field.required"));
                assertThat(responseEntity.getBody().get("errors").get("features").textValue())
                        .isEqualTo(messages.get("field.features.failure"));
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            }
        }
    }


}
