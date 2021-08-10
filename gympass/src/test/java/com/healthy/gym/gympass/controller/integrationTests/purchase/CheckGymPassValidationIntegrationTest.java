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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
class CheckGymPassValidationIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
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

    private String timeLimitedGymPassDocumentId;
    private String alreadySuspendedPurchasedGymPassDocumentId;
    private String timeLimitedWithRetroDateGymPassDocumentId;
    private String entriesLimitedPurchasedGymPassDocumentId;
    private String alreadySuspendedEntriesLimitedPurchasedGymPassDocumentId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {

        userToken = tokenFactory.getUserToken(UUID.randomUUID().toString());
        employeeToken = tokenFactory.getEmployeeToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        PurchasedGymPassDocument timeLimitedTypePurchasedGymPassDocument;
        PurchasedGymPassDocument alreadySuspendedPurchasedGymPassDocument;
        PurchasedGymPassDocument timeLimitedWithRetroDateGymPassDocument;
        PurchasedGymPassDocument entriesLimitedTypePurchasedGymPassDocument;
        PurchasedGymPassDocument alreadySuspendedEntriesLimitedTypePurchasedGymPassDocument;

        String gymPassOfferId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String name = "Jan";
        String surname = "Kowalski";
        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocument);

        String title = "Karnet miesięczny";
        double amount = 139.99;
        String currency = "zł";
        String period = "miesiąc";
        boolean isPremium = false;
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

        timeLimitedGymPassDocumentId = UUID.randomUUID().toString();
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = startDate.plusMonths(1);
        int entriesTimeLimitedGymPass = Integer.MAX_VALUE;
        timeLimitedTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                timeLimitedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(timeLimitedTypePurchasedGymPassDocument);

        alreadySuspendedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        alreadySuspendedPurchasedGymPassDocument = new PurchasedGymPassDocument(
                alreadySuspendedPurchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass,
                LocalDate.now().plusDays(10)
        );
        mongoTemplate.save(alreadySuspendedPurchasedGymPassDocument);

        timeLimitedWithRetroDateGymPassDocumentId = UUID.randomUUID().toString();
        timeLimitedWithRetroDateGymPassDocument = new PurchasedGymPassDocument(
                timeLimitedWithRetroDateGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                LocalDate.now().minusDays(1),
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(timeLimitedWithRetroDateGymPassDocument);

        entriesLimitedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        String endDateFroEntriesLimitedDocuments = "9999-12-31";
        int entriesForEntriesLimitedGymPass = 10;
        entriesLimitedTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                entriesLimitedPurchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                LocalDate.parse(endDateFroEntriesLimitedDocuments, DateTimeFormatter.ISO_LOCAL_DATE),
                entriesForEntriesLimitedGymPass
        );
        mongoTemplate.save(entriesLimitedTypePurchasedGymPassDocument);

        alreadySuspendedEntriesLimitedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        alreadySuspendedEntriesLimitedTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                alreadySuspendedEntriesLimitedPurchasedGymPassDocumentId,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                LocalDate.parse(endDateFroEntriesLimitedDocuments, DateTimeFormatter.ISO_LOCAL_DATE),
                entriesForEntriesLimitedGymPass,
                LocalDate.now().plusDays(10)
        );
        mongoTemplate.save(alreadySuspendedEntriesLimitedTypePurchasedGymPassDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldReturnStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValidStatus_whenNotSuspendedTimeLimitedGympassAndValidEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/status/"+timeLimitedGymPassDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.valid");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("result")).isNotNull();
            assertThat(responseEntity.getBody().get("result").get("valid").asBoolean()).isTrue();
            assertThat(responseEntity.getBody().get("result").get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get("result").get("entries").intValue())
                    .isZero();
            assertThat(responseEntity.getBody().get("result").get("suspensionDate"))
                    .isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidStatus_whenSuspendedTimeLimitedGympassAndValidEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/status/"
                    +alreadySuspendedPurchasedGymPassDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.not.valid");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("result")).isNotNull();
            assertThat(responseEntity.getBody().get("result").get("valid").asBoolean()).isFalse();
            assertThat(responseEntity.getBody().get("result").get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get("result").get("entries").intValue())
                    .isZero();
            assertThat(responseEntity.getBody().get("result").get("suspensionDate").textValue())
                    .isEqualTo(LocalDate.now().plusDays(10).toString());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidStatus_whenNotSuspendedTimeLimitedGympassAndInvalidEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/status/"
                    +timeLimitedWithRetroDateGymPassDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.not.valid");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("result")).isNotNull();
            assertThat(responseEntity.getBody().get("result").get("valid").asBoolean()).isFalse();
            assertThat(responseEntity.getBody().get("result").get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(1).toString());
            assertThat(responseEntity.getBody().get("result").get("entries").intValue())
                    .isZero();
            assertThat(responseEntity.getBody().get("result").get("suspensionDate"))
                    .isNull();
        }


        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnValidStatus_whenNotSuspendedEntriesLimitedGympassAndValidEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/status/"
                    +entriesLimitedPurchasedGymPassDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.valid");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("result")).isNotNull();
            assertThat(responseEntity.getBody().get("result").get("valid").asBoolean()).isTrue();
            assertThat(responseEntity.getBody().get("result").get("endDate")).isNull();
            assertThat(responseEntity.getBody().get("result").get("entries").intValue())
                    .isEqualTo(10);
            assertThat(responseEntity.getBody().get("result").get("suspensionDate"))
                    .isNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldReturnNotValidStatus_whenSuspendedEntriesLimitedGympassAndValidEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/status/"
                    +alreadySuspendedEntriesLimitedPurchasedGymPassDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("gympass.not.valid");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);


            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("result")).isNotNull();
            assertThat(responseEntity.getBody().get("result").get("valid").asBoolean()).isFalse();
            assertThat(responseEntity.getBody().get("result").get("endDate")).isNull();
            assertThat(responseEntity.getBody().get("result").get("entries").intValue())
                    .isEqualTo(10);
            assertThat(responseEntity.getBody().get("result").get("suspensionDate").textValue())
                    .isEqualTo(LocalDate.now().plusDays(10).toString());
        }

    }

    @Nested
    class ShouldNotReturnStatus{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotReturnGymPassValidationStatus_whenInvalidId(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/purchase/status/" +invalidId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            String expectedMessage = messages.get("exception.gympass.not.found");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }
    }
}
