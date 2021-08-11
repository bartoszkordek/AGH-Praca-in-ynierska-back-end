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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
@Tag("integration")
public class DeletePurchasedGymPassIntegrationTest {

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

    private String purchasedGymPassDocumentIdToRemove;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {

        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        employeeToken = tokenFactory.getEmployeeToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

        String gymPassOfferId = UUID.randomUUID().toString();
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
        String subheader = "Najepszy wybór dla regularnie uprawiających sport";
        boolean isPremium = false;
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

        purchasedGymPassDocumentIdToRemove = UUID.randomUUID().toString();
        LocalDateTime purchaseDateAndTime = LocalDateTime.now();
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = startDate.plusMonths(1);
        int entriesTimeLimitedGymPass = Integer.MAX_VALUE;
        PurchasedGymPassDocument purchasedGymPassDocumentToRemove = new PurchasedGymPassDocument(
                purchasedGymPassDocumentIdToRemove,
                gymPassOfferDocument,
                userDocument,
                purchaseDateAndTime,
                startDate,
                endDate,
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(purchasedGymPassDocumentToRemove);

        //document which should not be removed
        String gymPassOfferIdNotToBeRemoved = UUID.randomUUID().toString();
        String titleNotToRemove = "Karnet miesięczny PLUS";
        double amountNotToRemove = 149.99;
        boolean isPremiumNotToRemove = true;
        List<String> featuresForNotToBeRemoved = List.of("siłownia", "fitness", "TRX", "rowery", "sauna");
        GymPassDocument gymPassOfferDocumentNotToBeRemoved = new GymPassDocument(
                gymPassOfferIdNotToBeRemoved,
                titleNotToRemove,
                subheader,
                new Price(amountNotToRemove, currency, period),
                isPremiumNotToRemove,
                new Description(synopsis, featuresForNotToBeRemoved)
        );

        mongoTemplate.save(gymPassOfferDocumentNotToBeRemoved);

        String purchasedGymPassDocumentIdNotToBeRemoved = UUID.randomUUID().toString();
        LocalDate startDateNotToBeRemoved = LocalDate.now().minusDays(60);
        LocalDate endDateNotToBeRemoved = startDate.minusDays(60).plusMonths(1);
        PurchasedGymPassDocument purchasedGymPassDocumentNotToBeRemoved = new PurchasedGymPassDocument(
                purchasedGymPassDocumentIdNotToBeRemoved,
                gymPassOfferDocumentNotToBeRemoved,
                userDocument,
                purchaseDateAndTime,
                startDateNotToBeRemoved,
                endDateNotToBeRemoved,
                entriesTimeLimitedGymPass
        );
        mongoTemplate.save(purchasedGymPassDocumentNotToBeRemoved);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldDeleteGymPasse{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldDeleteUserGymPasse_whenValidId(TestCountry country)
                throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase/" + purchasedGymPassDocumentIdToRemove);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", employeeToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);

            List<PurchasedGymPassDocument> gymPassDocumentListBefore = mongoTemplate.findAll(PurchasedGymPassDocument.class);
            assertThat(gymPassDocumentListBefore.size()).isEqualTo(2);

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            String expectedMessage = messages.get("gympass.removed");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isNotNull();
            assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("purchasedGymPass")).isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("purchasedGymPassDocumentId").textValue())
                    .isEqualTo(purchasedGymPassDocumentIdToRemove);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("gymPassOfferId"))
                    .isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("title").textValue())
                    .isEqualTo("Karnet miesięczny");
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("amount").doubleValue())
                    .isEqualTo(139.99);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("gymPassOffer").get("price").get("period").textValue())
                    .isEqualTo("miesiąc");
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("purchaseDateAndTime")).isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("startDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).toString());
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("endDate").textValue())
                    .isEqualTo(LocalDate.now().minusDays(2).plusMonths(1).toString());
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("entries").intValue())
                    .isEqualTo(Integer.MAX_VALUE);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("suspensionDate")).isNull();

            List<PurchasedGymPassDocument> gymPassDocumentListAfter = mongoTemplate.findAll(PurchasedGymPassDocument.class);
            assertThat(gymPassDocumentListAfter.size()).isEqualTo(1);
        }
    }
}
