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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
public class GetUserLatestGymPassIntegrationTest {

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

    private String userId;
    private String userIdWithNotPurchasedGymPasses;


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        employeeToken = tokenFactory.getEmployeeToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        String gymPassOfferId1 = UUID.randomUUID().toString();
        String gymPassOfferId2 = UUID.randomUUID().toString();
        String name = "Jan";
        String surname = "Kowalski";
        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocument);

        userIdWithNotPurchasedGymPasses = UUID.randomUUID().toString();
        UserDocument userWithNotPurchasedGymPasses = new UserDocument();
        userWithNotPurchasedGymPasses.setName("No");
        userWithNotPurchasedGymPasses.setSurname("GymPasses");
        userWithNotPurchasedGymPasses.setUserId(userIdWithNotPurchasedGymPasses);
        userWithNotPurchasedGymPasses.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userWithNotPurchasedGymPasses);

        String title1 = "Karnet miesięczny";
        String title2 = "Karnet semestralny";
        double amount1 = 139.99;
        double amount2 = 399.99;
        String currency = "zł";
        String period1 = "miesiąc";
        String period2 = "semestr";
        boolean isPremium = false;
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        String synopsisTimeLimited = "Nielimitowana liczba wejść";
        List<String> features = List.of("siłownia", "fitness", "TRX", "rowery");
        GymPassDocument gymPassOfferDocument1 = new GymPassDocument(
                gymPassOfferId1,
                title1,
                subheader,
                new Price(amount1, currency, period1),
                isPremium,
                new Description(synopsisTimeLimited, features)
        );
        GymPassDocument gymPassOfferDocument2 = new GymPassDocument(
                gymPassOfferId2,
                title2,
                subheader,
                new Price(amount2, currency, period2),
                isPremium,
                new Description(synopsisTimeLimited, features)
        );

        mongoTemplate.save(gymPassOfferDocument1);
        mongoTemplate.save(gymPassOfferDocument2);

        String gymPassDocumentId1 = UUID.randomUUID().toString();
        LocalDateTime purchaseDateTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = startDate.plusMonths(1);
        int entriesTimeLimitedGymPass = Integer.MAX_VALUE;
        PurchasedGymPassDocument purchasedGymPassDocument1 = new PurchasedGymPassDocument(
                gymPassDocumentId1,
                gymPassOfferDocument1,
                userDocument,
                purchaseDateTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(purchasedGymPassDocument1);

        String purchasedGymPassDocumentId2 = UUID.randomUUID().toString();
        PurchasedGymPassDocument purchasedGymPassDocument2 = new PurchasedGymPassDocument(
                purchasedGymPassDocumentId2,
                gymPassOfferDocument2,
                userDocument,
                purchaseDateTime,
                LocalDate.now().minusMonths(5),
                LocalDate.now().plusDays(5),
                Integer.MAX_VALUE
        );
        mongoTemplate.save(purchasedGymPassDocument2);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetUserLatestGymPass_whenValidUserId(TestCountry country)
            throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/purchase/user/" + userId + "/latest");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        assertThat(responseEntity.getBody().get("purchasedGymPassDocumentId")).isNotNull();
        assertThat(responseEntity.getBody().get("gymPassOffer").get("gymPassOfferId")).isNotNull();
        assertThat(responseEntity.getBody().get("gymPassOffer").get("title").textValue())
                .isEqualTo("Karnet semestralny");
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("amount").doubleValue())
                .isEqualTo(399.99);
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("currency").textValue())
                .isEqualTo("zł");
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("period").textValue())
                .isEqualTo("semestr");
        assertThat(responseEntity.getBody().get("purchaseDateTime")).isNotNull();
        assertThat(responseEntity.getBody().get("startDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(5).toString());
        assertThat(responseEntity.getBody().get("endDate").textValue())
                .isEqualTo(LocalDate.now().plusDays(5).toString());
        assertThat(responseEntity.getBody().get("entries").intValue())
                .isEqualTo(Integer.MAX_VALUE);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetUserLatestGymPass_whenValidUserIdAndLoggedAsSpecificUser(TestCountry country)
            throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/purchase/user/" + userId + "/latest");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        assertThat(responseEntity.getBody().get("purchasedGymPassDocumentId")).isNotNull();
        assertThat(responseEntity.getBody().get("gymPassOffer").get("gymPassOfferId")).isNotNull();
        assertThat(responseEntity.getBody().get("gymPassOffer").get("title").textValue())
                .isEqualTo("Karnet semestralny");
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("amount").doubleValue())
                .isEqualTo(399.99);
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("currency").textValue())
                .isEqualTo("zł");
        assertThat(responseEntity.getBody().get("gymPassOffer").get("price").get("period").textValue())
                .isEqualTo("semestr");
        assertThat(responseEntity.getBody().get("purchaseDateTime")).isNotNull();
        assertThat(responseEntity.getBody().get("startDate").textValue())
                .isEqualTo(LocalDate.now().minusMonths(5).toString());
        assertThat(responseEntity.getBody().get("endDate").textValue())
                .isEqualTo(LocalDate.now().plusDays(5).toString());
        assertThat(responseEntity.getBody().get("entries").intValue())
                .isEqualTo(Integer.MAX_VALUE);
    }

    @Nested
    class ShouldNotGetLastGymPass {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetUserLatestGymPass_whenInvalidUserId(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidUserId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/purchase/user/" + invalidUserId + "/latest");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.user.not.found");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(400);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetUserLatestGymPass_whenEmptyList(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/user/" + userIdWithNotPurchasedGymPasses + "/latest");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseEntity.getBody()).isNotNull();
        }


        @Nested
        class ShouldNotGetUserLatestGymPassWhenNotAuthorized {

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetUserLatestGymPassWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/purchase/user/" + userId + "/latest");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.GET, request, JsonNode.class);


                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo("Access Denied");
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetUserLatestGymPassWhenNoTokenLoggedAsOtherUser(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/purchase/user/" + userIdWithNotPurchasedGymPasses
                        + "/latest");

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.GET, request, JsonNode.class);


                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message")).isNotNull();
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
            }
        }

    }
}
