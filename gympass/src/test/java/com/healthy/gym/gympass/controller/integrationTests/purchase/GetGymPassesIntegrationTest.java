package com.healthy.gym.gympass.controller.integrationTests.purchase;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.dto.BasicUserInfoDTO;
import com.healthy.gym.gympass.dto.PurchasedGymPassDTO;
import com.healthy.gym.gympass.dto.SimpleGymPassDTO;
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
public class GetGymPassesIntegrationTest {

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

    private List<PurchasedGymPassDTO> responseList;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {

        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        employeeToken = tokenFactory.getEmployeeToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        PurchasedGymPassDocument timeLimitedTypePurchasedGymPassDocument;
        PurchasedGymPassDocument alreadySuspendedPurchasedGymPassDocument;
        PurchasedGymPassDocument entriesLimitedTypePurchasedGymPassDocument;

        String gymPassOfferId1 = UUID.randomUUID().toString();
        String gymPassOfferId2 = UUID.randomUUID().toString();
        String gymPassOfferId3 = UUID.randomUUID().toString();
        String name = "Jan";
        String surname = "Kowalski";
        UserDocument userDocument = new UserDocument();
        userDocument.setName(name);
        userDocument.setSurname(surname);
        userDocument.setUserId(userId);
        userDocument.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocument);

        String userIdNotToPick = UUID.randomUUID().toString();
        UserDocument userDocumentWithLastYearGymPass = new UserDocument();
        userDocumentWithLastYearGymPass.setName("Last");
        userDocumentWithLastYearGymPass.setSurname("Year");
        userDocumentWithLastYearGymPass.setUserId(userIdNotToPick);
        userDocumentWithLastYearGymPass.setGymRoles(List.of(GymRole.USER));

        mongoTemplate.save(userDocumentWithLastYearGymPass);

        String title1 = "Karnet miesięczny";
        String title2 = "Karnet semestralny";
        String title3 = "Karnet 10 wejść";
        double amount1 = 139.99;
        double amount2 = 399.99;
        double amount3 = 99.99;
        String currency = "zł";
        String period1 = "miesiąc";
        String period2 = "semestr";
        String period3 = "nielimitowany";
        boolean isPremium = false;
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        String synopsisTimeLimited = "Nielimitowana liczba wejść";
        String synopsisEntriesLimited = "Wykorzystaj kiego chcesz.";
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
        GymPassDocument gymPassOfferDocument3 = new GymPassDocument(
                gymPassOfferId3,
                title3,
                subheader,
                new Price(amount3, currency, period3),
                isPremium,
                new Description(synopsisEntriesLimited, features)
        );

        String gymPassOfferNotToBePicked = UUID.randomUUID().toString();
        GymPassDocument gymPassOfferDocumentNotToPick = new GymPassDocument(
                gymPassOfferNotToBePicked,
                "nowy karnet miesięczny",
                subheader,
                new Price(amount1, currency, period1),
                isPremium,
                new Description(synopsisEntriesLimited, features)
        );

        mongoTemplate.save(gymPassOfferDocument1);
        mongoTemplate.save(gymPassOfferDocument2);
        mongoTemplate.save(gymPassOfferDocument3);
        mongoTemplate.save(gymPassOfferDocumentNotToPick);

        String timeLimitedGymPassDocumentId = UUID.randomUUID().toString();
        LocalDateTime purchaseDateTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = startDate.plusMonths(1);
        int entriesTimeLimitedGymPass = Integer.MAX_VALUE;
        timeLimitedTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                timeLimitedGymPassDocumentId,
                gymPassOfferDocument1,
                userDocument,
                purchaseDateTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(timeLimitedTypePurchasedGymPassDocument);
        PurchasedGymPassDTO purchasedGymPassDTO1 = new PurchasedGymPassDTO(
                timeLimitedGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId1,
                        title1,
                        new Price(amount1, currency, period1),
                        false
                ),
                new BasicUserInfoDTO(
                        userId,
                        name,
                        surname
                ),
                purchaseDateTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass,
                null
        );

        String alreadySuspendedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        LocalDate endDateForSemesterSuspendedGymPass = startDate.plusMonths(6).plusDays(10);
        alreadySuspendedPurchasedGymPassDocument = new PurchasedGymPassDocument(
                alreadySuspendedPurchasedGymPassDocumentId,
                gymPassOfferDocument2,
                userDocument,
                purchaseDateTime,
                startDate,
                endDateForSemesterSuspendedGymPass,
                entriesTimeLimitedGymPass,
                LocalDate.now().plusDays(10)
        );
        mongoTemplate.save(alreadySuspendedPurchasedGymPassDocument);
        PurchasedGymPassDTO purchasedGymPassDTO2 = new PurchasedGymPassDTO(
                alreadySuspendedPurchasedGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId2,
                        title2,
                        new Price(amount2, currency, period2),
                        false
                ),
                new BasicUserInfoDTO(
                        userId,
                        name,
                        surname
                ),
                purchaseDateTime,
                startDate,
                startDate.plusMonths(6).plusDays(10),
                entriesTimeLimitedGymPass,
                LocalDate.now().plusDays(10)
        );

        String entriesLimitedPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        String endDateForEntriesLimitedDocuments = "9999-12-31";
        int entriesForEntriesLimitedGymPass = 10;
        entriesLimitedTypePurchasedGymPassDocument = new PurchasedGymPassDocument(
                entriesLimitedPurchasedGymPassDocumentId,
                gymPassOfferDocument3,
                userDocument,
                purchaseDateTime,
                startDate,
                LocalDate.parse(endDateForEntriesLimitedDocuments, DateTimeFormatter.ISO_LOCAL_DATE),
                entriesForEntriesLimitedGymPass
        );
        mongoTemplate.save(entriesLimitedTypePurchasedGymPassDocument);
        PurchasedGymPassDTO purchasedGymPassDTO3 = new PurchasedGymPassDTO(
                entriesLimitedPurchasedGymPassDocumentId,
                new SimpleGymPassDTO(
                        gymPassOfferId3,
                        title3,
                        new Price(amount3, currency, period3),
                        false
                ),
                new BasicUserInfoDTO(
                        userId,
                        name,
                        surname
                ),
                purchaseDateTime,
                startDate,
                LocalDate.parse(endDateForEntriesLimitedDocuments, DateTimeFormatter.ISO_LOCAL_DATE),
                entriesForEntriesLimitedGymPass,
                null
        );


        responseList = List.of(
                purchasedGymPassDTO1,
                purchasedGymPassDTO2,
                purchasedGymPassDTO3
        );

        String lastYearPurchasedGymPassDocumentId = UUID.randomUUID().toString();
        PurchasedGymPassDocument lastYearPurchasedGymPassDocument = new PurchasedGymPassDocument(
                lastYearPurchasedGymPassDocumentId,
                gymPassOfferDocumentNotToPick,
                userDocumentWithLastYearGymPass,
                purchaseDateTime.minusYears(1),
                LocalDate.now().minusYears(1),
                LocalDate.now().minusYears(1).plusMonths(1),
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(lastYearPurchasedGymPassDocument);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetGymPasses_whenValidUserIdNotEmptyListAndNoRequestDates(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            int page = 0;

            URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();

            assertThat(responseEntity.getBody().get(0).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet miesięczny");
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(139.99);
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("miesiąc");
            assertThat(responseEntity.getBody().get(0).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(0).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(0).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(0).get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get(0).get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get(0).get("suspensionDate")).isNull();

            assertThat(responseEntity.getBody().get(1).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet semestralny");
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(399.99);
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("semestr");
            assertThat(responseEntity.getBody().get(1).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(1).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(1).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(1).get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(6).plusDays(10).toString());
            assertThat(responseEntity.getBody().get(1).get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get(1).get("suspensionDate").textValue())
                    .isEqualTo(LocalDate.now().plusDays(10).toString());

            assertThat(responseEntity.getBody().get(2).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet 10 wejść");
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(99.99);
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("nielimitowany");
            assertThat(responseEntity.getBody().get(2).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(2).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(2).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(2).get("endDate").textValue())
                    .isEqualTo(LocalDate.parse("9999-12-31", DateTimeFormatter.ISO_LOCAL_DATE).toString());
            assertThat(responseEntity.getBody().get(2).get("entries").intValue())
                    .isEqualTo(10);
            assertThat(responseEntity.getBody().get(2).get("suspensionDate")).isNull();

            assertThat(responseEntity.getBody().size()).isEqualTo(3);

            List<PurchasedGymPassDocument> gymPassDocumentList = mongoTemplate.findAll(PurchasedGymPassDocument.class);
            assertThat(gymPassDocumentList.size()).isEqualTo(4);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldGetGymPasses_whenValidUserIdNotEmptyListAndValidRequestDates(TestCountry country)
                throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            String purchaseStartDate = "2000-01-01";
            String purchaseEndDate = "2030-12-31";
            int page = 0;

            URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page+
                    "?purchaseStartDate="+purchaseStartDate+"&purchaseEndDate="+purchaseEndDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();

            assertThat(responseEntity.getBody().get(0).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet miesięczny");
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(139.99);
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(0).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("miesiąc");
            assertThat(responseEntity.getBody().get(0).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(0).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(0).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(0).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(0).get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get(0).get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get(0).get("suspensionDate")).isNull();

            assertThat(responseEntity.getBody().get(1).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet semestralny");
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(399.99);
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(1).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("semestr");
            assertThat(responseEntity.getBody().get(1).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(1).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(1).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(1).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(1).get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(6).plusDays(10).toString());
            assertThat(responseEntity.getBody().get(1).get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get(1).get("suspensionDate").textValue())
                    .isEqualTo(LocalDate.now().plusDays(10).toString());

            assertThat(responseEntity.getBody().get(2).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet 10 wejść");
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(99.99);
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(2).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("nielimitowany");
            assertThat(responseEntity.getBody().get(2).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("user").get("name").textValue())
                    .isEqualTo("Jan");
            assertThat(responseEntity.getBody().get(2).get("user").get("surname").textValue())
                    .isEqualTo("Kowalski");
            assertThat(responseEntity.getBody().get(2).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(2).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get(2).get("endDate").textValue())
                    .isEqualTo(LocalDate.parse("9999-12-31", DateTimeFormatter.ISO_LOCAL_DATE).toString());
            assertThat(responseEntity.getBody().get(2).get("entries").intValue())
                    .isEqualTo(10);
            assertThat(responseEntity.getBody().get(2).get("suspensionDate")).isNull();

            assertThat(responseEntity.getBody().get(3).get("purchasedGymPassDocumentId")).isNotNull();
            assertThat(responseEntity.getBody().get(3).get("gymPassOffer").get("gymPassOfferId")).isNotNull();
            assertThat(responseEntity.getBody().get(3).get("gymPassOffer").get("title").textValue())
                    .isEqualTo("nowy karnet miesięczny");
            assertThat(responseEntity.getBody().get(3).get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(139.99);
            assertThat(responseEntity.getBody().get(3).get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get(3).get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("miesiąc");
            assertThat(responseEntity.getBody().get(3).get("user").get("userId")).isNotNull();
            assertThat(responseEntity.getBody().get(3).get("user").get("name").textValue())
                    .isEqualTo("Last");
            assertThat(responseEntity.getBody().get(3).get("user").get("surname").textValue())
                    .isEqualTo("Year");
            assertThat(responseEntity.getBody().get(3).get("purchaseDateTime")).isNotNull();
            assertThat(responseEntity.getBody().get(3).get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusYears(1).toString());
            assertThat(responseEntity.getBody().get(3).get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusYears(1).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get(3).get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get(3).get("suspensionDate")).isNull();

            assertThat(responseEntity.getBody().size()).isEqualTo(4);

            List<PurchasedGymPassDocument> gymPassDocumentList = mongoTemplate.findAll(PurchasedGymPassDocument.class);
            assertThat(gymPassDocumentList.size()).isEqualTo(4);
        }
    }


    @Nested
    class ShouldNotGetGymPasses{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetGymPasses_whenStartDateAfterEndDate(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String purchaseStartDate = "2030-12-31";
            String purchaseEndDate = "2000-01-01";
            int page = 0;

            URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page+
                    "?purchaseStartDate="+purchaseStartDate+"&purchaseEndDate="+purchaseEndDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            String expectedMessage = messages.get("exception.start.after.end");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(400);
            assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Bad Request");
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldNotGetGymPasses_whenEmptyList(TestCountry country) throws URISyntaxException {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String purchaseStartDate = "2100-01-01";
            String purchaseEndDate = "2100-02-01";
            int page = 0;

            URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page+
                    "?purchaseStartDate="+purchaseStartDate+"&purchaseEndDate="+purchaseEndDate);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(responseEntity.getBody()).isNull();

        }

        @Nested
        class ShouldNotDeleteGymPassWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetGymPassesWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                int page = 0;

                URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page);

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
            void shouldNotGetGymPassesWhenLoggedAsUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                int page = 0;

                URI uri = new URI("http://localhost:" + port + "/purchase/page/"+page);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.GET, request, JsonNode.class);

                String expectedMessage = messages.get("exception.access.denied");

                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                assertThat(responseEntity.getBody().get("status").intValue()).isEqualTo(403);
                assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Forbidden");
                assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
                assertThat(responseEntity.getBody().get("timestamp")).isNotNull();

                List<PurchasedGymPassDocument> gymPassDocumentListAfter = mongoTemplate.findAll(PurchasedGymPassDocument.class);
            }
        }
    }
}
