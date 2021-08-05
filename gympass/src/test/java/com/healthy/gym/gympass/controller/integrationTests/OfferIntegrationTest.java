package com.healthy.gym.gympass.controller.integrationTests;

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
class OfferIntegrationTest {

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
    private String managerToken;
    private String requestContent;
    private String invalidTitleRequestContent;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());

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

        GymPassOfferRequest invalidGymPassOfferRequest = new GymPassOfferRequest();
        invalidGymPassOfferRequest.setTitle("T");

        invalidTitleRequestContent = objectMapper.writeValueAsString(invalidGymPassOfferRequest);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GymPassDocument.class);
    }

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
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowBindExceptionWhenInvalidTitle(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/offer");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<Object> request = new HttpEntity<>(invalidTitleRequestContent, headers);
        String expectedMessage = messages.get("request.bind.exception");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue())).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("errors").get("title").textValue())
                .isEqualTo(messages.get("field.name.failure"));
        assertThat(responseEntity.getBody().get("errors").get("period").textValue())
                .isEqualTo(messages.get("field.required"));
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }
}
