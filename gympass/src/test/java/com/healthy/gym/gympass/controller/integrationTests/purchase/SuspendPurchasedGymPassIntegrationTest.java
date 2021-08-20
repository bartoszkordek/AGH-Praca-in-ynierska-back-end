package com.healthy.gym.gympass.controller.integrationTests.purchase;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.enums.GymRole;
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
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
class SuspendPurchasedGymPassIntegrationTest {

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
    private String employeeToken;
    private String managerToken;
    private String gymPassOfferId;
    private String userId;
    private String title;
    private double amount;
    private String currency;
    private String period;
    private boolean isPremium;
    private String name;
    private String surname;
    private String purchasedGymPassDocumentId;
    private LocalDateTime purchaseDateTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private int entries;
    private PurchasedGymPassDocument purchasedGymPassDocument;
    private String alreadySuspendedPurchasedGymPassDocumentId;
    private PurchasedGymPassDocument alreadySuspendedPurchasedGymPassDocument;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {

        userToken = tokenFactory.getUserToken(UUID.randomUUID().toString());
        employeeToken = tokenFactory.getEmployeeToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        gymPassOfferId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();
        name = "Jan";
        surname = "Kowalski";
        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocument);

        title = "Karnet miesięczny";
        amount = 139.99;
        currency = "zł";
        period = "miesiąc";
        isPremium = false;
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        String synopsis = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");
        GymPassDocument gymPassOfferDocument = new GymPassDocument(
                gymPassOfferId,
                title,
                subheader,
                new Price(amount, currency, period),
                isPremium,
                new Description(synopsis, features)
        );

        mongoTemplate.save(gymPassOfferDocument);

        purchasedGymPassDocumentId = UUID.randomUUID().toString();
        purchaseDateTime = LocalDateTime.now();
        startDate = LocalDate.now().minusDays(2);
        endDate = startDate.plusMonths(1);
        entries = Integer.MAX_VALUE;
        purchasedGymPassDocument = new PurchasedGymPassDocument(
                purchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateTime,
                startDate,
                endDate,
                entries
        );

        mongoTemplate.save(purchasedGymPassDocument);

        alreadySuspendedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        purchaseDateTime = LocalDateTime.now();
        startDate = LocalDate.now().minusDays(2);
        endDate = startDate.plusMonths(1);
        entries = Integer.MAX_VALUE;
        alreadySuspendedPurchasedGymPassDocument = new PurchasedGymPassDocument(
                alreadySuspendedPurchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateTime,
                startDate,
                endDate,
                entries,
                LocalDate.now().plusDays(10)
        );

        mongoTemplate.save(alreadySuspendedPurchasedGymPassDocument);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldSuspendGymPass {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldSuspendTimeGymPass_whenValidRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentId +
                    "/suspend/" + suspensionDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.suspended");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("gymPassOfferId").textValue())
                    .isEqualTo(gymPassOfferId);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("title").textValue())
                    .isEqualTo(title);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(amount);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo(currency);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo(period);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("premium").booleanValue())
                    .isFalse();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("user").get("userId").textValue())
                    .isEqualTo(userId);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("user").get("name").textValue())
                    .isEqualTo(name);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("user").get("surname").textValue())
                    .isEqualTo(surname);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("purchaseDateTime"))
                    .isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("startDate").textValue())
                    .isEqualTo(startDate.toString());
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("endDate").textValue())
                    .isEqualTo(endDate.plusDays(2).toString());
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("entries").intValue())
                    .isEqualTo(entries);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("suspensionDate").textValue())
                    .isEqualTo(suspensionDate);
        }
    }

    @Nested
    class ShouldNotSuspendGymPass {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotSuspendGymPassWhenInvalidPurchasedGymPassId(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidOfferIdPurchasedGymPassId = UUID.randomUUID().toString();
            String suspensionDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + invalidOfferIdPurchasedGymPassId +
                    "/suspend/" + suspensionDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            String expectedMessage = messages.get("exception.gympass.not.found");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotSuspendGymPassWhenPurchasedGymPassAlreadySuspended(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + alreadySuspendedPurchasedGymPassDocumentId +
                    "/suspend/" + suspensionDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            String expectedMessage = messages.get("exception.gympass.already.suspended");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotSuspendGymPassWhenRetroSuspensionDate(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentId +
                    "/suspend/" + suspensionDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            String expectedMessage = messages.get("exception.retro.date.suspension");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotSuspendGymPassWhenSuspensionDateAfterEndDate(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String suspensionDate = LocalDate.now().plusYears(1000).format(DateTimeFormatter.ISO_LOCAL_DATE);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentId +
                    "/suspend/" + suspensionDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            String expectedMessage = messages.get("exception.suspension.after.end");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.PUT, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @Nested
        class ShouldNotSuspendGymPassWhenNotAuthorized {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotSuspendGymPassWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                String suspensionDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);

                URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentId +
                        "/suspend/" + suspensionDate);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

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
            void shouldNotSuspendGymPassWhenLoggedAsUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                String suspensionDate = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE);

                URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentId +
                        "/suspend/" + suspensionDate);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

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
    }
}
