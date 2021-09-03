package com.healthy.gym.gympass.controller.integrationTests.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.pojo.request.GymPassOfferRequest;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.*;
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
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
@Tag("integration")
class CreateOfferIntegrationTest {

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

        ObjectMapper objectMapper = new ObjectMapper();

        GymPassOfferRequest gymPassOfferRequest = new GymPassOfferRequest();
        gymPassOfferRequest.setTitle("Karnet miesięczny");
        gymPassOfferRequest.setSubheader("Najlepszy wybór dla osób aktywnych");
        gymPassOfferRequest.setAmount(139.99);
        gymPassOfferRequest.setCurrency("zł");
        gymPassOfferRequest.setPeriod("miesiąc");
        gymPassOfferRequest.setIsPremium(true);
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
    class ShouldCreateOffer {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldCreateNewGymPassOffer(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/offer");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("offer.created");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

            JsonNode body = responseEntity.getBody();
            assert body != null;

            assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

            JsonNode gymPass = body.get("gymPass");

            assertThat(gymPass.get("title").textValue()).isEqualTo("Karnet miesięczny");
            assertThat(gymPass.get("subheader").textValue())
                    .isEqualTo("Najlepszy wybór dla osób aktywnych");

            JsonNode price = gymPass.get("price");
            assertThat(price.get("amount").asDouble()).isEqualTo(139.99);
            assertThat(price.get("currency").textValue()).isEqualTo("zł");
            assertThat(price.get("period").textValue()).isEqualTo("miesiąc");

            assertThat(gymPass.get("premium").booleanValue()).isTrue();

            JsonNode description = gymPass.get("description");
            assertThat(description.get("synopsis").textValue())
                    .isEqualTo("Karnet uprawniający do korzystania w pełni z usług ośrodka");

            JsonNode features = description.get("features");
            assertThat(features.get(0).textValue()).isEqualTo("Full pakiet");
            assertThat(features.get(1).textValue()).isEqualTo("sauna");
            assertThat(features.get(2).textValue()).isEqualTo("siłownia");
            assertThat(features.get(3).textValue()).isEqualTo("basen");

            List<GymPassDocument> gymPassDocumentList = mongoTemplate.findAll(GymPassDocument.class);
            assertThat(gymPassDocumentList.size()).isEqualTo(1);
        }
    }


    @Nested
    class ShouldNotCreateOffer {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowDuplicatedGymPassDocumentsWhenCreateNewOffer(TestCountry country) throws Exception {
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

            URI uri = new URI("http://localhost:" + port + "/offer");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);
            String expectedMessage = messages.get("exception.duplicated.offers");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

            JsonNode body = responseEntity.getBody();
            assert body != null;
            assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
        }

        @Nested
        class ShouldNotCreateOfferWhenNotAuthorized {

            private URI uri;

            @BeforeEach
            void setUp() throws URISyntaxException {
                uri = new URI("http://localhost:" + port + "/offer");
            }

            @Test
            void shouldNotGetOffersWhenNoToken() {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", Locale.ENGLISH.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

                JsonNode body = responseEntity.getBody();
                assert body != null;

                assertThat(body.get("status").intValue()).isEqualTo(403);
                assertThat(body.get("error").textValue()).isEqualTo("Forbidden");
                assertThat(body.get("message").textValue()).isEqualTo("Access Denied");
                assertThat(body.get("timestamp")).isNotNull();
            }

            @Test
            void shouldNotGetOffersWhenLoggedAsUser() {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", Locale.ENGLISH.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(requestContent, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

                JsonNode body = responseEntity.getBody();
                assert body != null;

                assertThat(body.get("status").intValue()).isEqualTo(403);
                assertThat(body.get("error").textValue()).isEqualTo("Forbidden");
                assertThat(body.get("timestamp")).isNotNull();

                Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
                String expectedMessage = messages.get("exception.access.denied");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
            }
        }

        @Nested
        class ShouldThrowBindException {

            private URI uri;
            private Map<String, String> messages;

            @BeforeEach
            void setUp() throws URISyntaxException {
                uri = new URI("http://localhost:" + port + "/offer");
                messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
            }

            private HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", Locale.ENGLISH.toString());
                headers.set("Authorization", managerToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                return headers;
            }

            @Test
            void shouldThrowBindExceptionWhenInvalidTitle() {
                HttpEntity<Object> request =
                        new HttpEntity<>(invalidTitleRequestContent, getHeaders());
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                testHeadersAndStatusCode(responseEntity);
                JsonNode body = responseEntity.getBody();
                assert body != null;

                String expectedMessage = messages.get("request.bind.exception");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.name.failure"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.required"));

            }

            private void testHeadersAndStatusCode(ResponseEntity<JsonNode> responseEntity) {
                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            }

            @Test
            void shouldThrowBindExceptionWhenInvalidSubheader() {
                HttpEntity<Object> request =
                        new HttpEntity<>(invalidSubheaderRequestContent, getHeaders());
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                testHeadersAndStatusCode(responseEntity);
                JsonNode body = responseEntity.getBody();
                assert body != null;

                String expectedMessage = messages.get("request.bind.exception");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.required"));
            }

            @Test
            void shouldThrowBindExceptionWhenInvalidPeriod() {
                HttpEntity<Object> request = new HttpEntity<>(invalidPeriodRequestContent, getHeaders());
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                testHeadersAndStatusCode(responseEntity);
                JsonNode body = responseEntity.getBody();
                assert body != null;

                String expectedMessage = messages.get("request.bind.exception");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.period.failure"));

            }

            @Test
            void shouldThrowBindExceptionWhenInvalidSynopsis() {
                HttpEntity<Object> request = new HttpEntity<>(invalidSynopsisRequestContent, getHeaders());
                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                testHeadersAndStatusCode(responseEntity);
                JsonNode body = responseEntity.getBody();
                assert body != null;

                String expectedMessage = messages.get("request.bind.exception");
                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("synopsis").textValue()).isEqualTo(messages.get("field.synopsis.failure"));
            }

            @Test
            void shouldThrowBindExceptionWhenInvalidFeatures() {
                HttpEntity<Object> request = new HttpEntity<>(invalidFeaturesRequestContent, getHeaders());
                String expectedMessage = messages.get("request.bind.exception");

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.POST, request, JsonNode.class);

                testHeadersAndStatusCode(responseEntity);
                JsonNode body = responseEntity.getBody();
                assert body != null;

                assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);

                JsonNode errors = body.get("errors");
                assertThat(errors.get("title").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("period").textValue()).isEqualTo(messages.get("field.required"));
                assertThat(errors.get("features").textValue()).isEqualTo(messages.get("field.features.failure"));
            }
        }

    }

}
