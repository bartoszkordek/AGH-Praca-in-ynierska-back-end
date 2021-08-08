package com.healthy.gym.gympass.controller.integrationTests.purchase;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.data.document.PurchasedGymPassDocument;
import com.healthy.gym.gympass.data.document.UserDocument;
import com.healthy.gym.gympass.enums.GymRole;
import com.healthy.gym.gympass.pojo.request.PurchasedGymPassRequest;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.LocalDate;
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
public class PurchaseGymPassIntegrationTest {

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
    private String timePurchasedGymPassRequestContent;
    private String gymPassOfferId;
    private String userId;
    private String title;
    private double amount;
    private String currency;
    private String period;
    private boolean isPremium;
    private String name;
    private String surname;
    private String requestStartDate;
    private String requestEndDate;
    private int entries;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {

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
                new Description(synopsis,features)
        );

        mongoTemplate.save(gymPassOfferDocument);

        ObjectMapper objectMapper = new ObjectMapper();

        requestStartDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        requestEndDate = LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_DATE);
        entries = Integer.MAX_VALUE;
        PurchasedGymPassRequest timePurchasedGymPassRequest = new PurchasedGymPassRequest();
        timePurchasedGymPassRequest.setGymPassOfferId(gymPassOfferId);
        timePurchasedGymPassRequest.setUserId(userId);
        timePurchasedGymPassRequest.setStartDate(requestStartDate);
        timePurchasedGymPassRequest.setEndDate(requestEndDate);
        timePurchasedGymPassRequest.setEntries(entries);

        timePurchasedGymPassRequestContent = objectMapper.writeValueAsString(timePurchasedGymPassRequest);
    }

    @AfterEach
    void tearDown(){
        mongoTemplate.dropCollection(PurchasedGymPassDocument.class);
        mongoTemplate.dropCollection(GymPassDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Nested
    class ShouldPurchaseGymPass{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldPurchaseTimeGymPass_whenValidRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/purchase");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(timePurchasedGymPassRequestContent, headers);
            String expectedMessage = messages.get("gympass.purchased");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.POST, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
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
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("purchaseDateAndTime"))
                    .isNotNull();
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("startDate").textValue())
                    .isEqualTo(requestStartDate);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("endDate").textValue())
                    .isEqualTo(requestEndDate);
            assertThat(responseEntity.getBody().get("purchasedGymPass").get("entries").intValue())
                    .isEqualTo(entries);
        }
    }

    @Nested
    class ShouldNotPurchaseGymPass{


    }
}
