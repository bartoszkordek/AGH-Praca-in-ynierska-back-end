package com.healthy.gym.gympass.controller.integrationTests.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.Messages;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.*;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
class GetOffersIntegrationTest {

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
    private String managerToken;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        userToken = tokenFactory.getUserToken(UUID.randomUUID().toString());
        managerToken = tokenFactory.getMangerToken(UUID.randomUUID().toString());
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GymPassDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotGetOffersWhenEmptyOffersLists(TestCountry country) throws Exception {

        Locale testedLocale = convertEnumToLocale(country);
        Map<String, String> messages = Messages.getMessagesAccordingToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/offer");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", managerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        String expectedMessage = messages.get("exception.no.offers");
        assertThat(Objects.requireNonNull(responseEntity.getBody()).get("message").textValue())
                .isEqualTo(expectedMessage);
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetGymPassOffers(TestCountry country) throws Exception {

        //before
        mongoTemplate.save(new GymPassDocument(
                UUID.randomUUID().toString(),
                "Karnet miesięczny",
                "Najlepszy wybór dla osób aktywnych",
                new Price(139.99, "zł", "miesiąc"),
                false,
                new Description("Karnet uprawniający do korzystania w pełni z usług ośrodka",
                        List.of("Full pakiet", "sauna", "siłownia", "basen"))
        ));

        mongoTemplate.save(new GymPassDocument(
                UUID.randomUUID().toString(),
                "Karnet kwartalny",
                "Najlepszy wybór dla osób aktywnych i korzystny cenowo",
                new Price(399.99, "zł", "miesiąc"),
                false,
                new Description("Karnet uprawniający do korzystania w pełni z usług ośrodka",
                        List.of("Full pakiet", "sauna", "siłownia", "basen"))
        ));

        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/offer");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody().get(0).get("title").textValue())).isEqualTo("Karnet miesięczny");
        assertThat(responseEntity.getBody().get(0).get("subheader").textValue())
                .isEqualTo("Najlepszy wybór dla osób aktywnych");
        assertThat(responseEntity.getBody().get(0).get("price").get("amount").asDouble()).isEqualTo(139.99);
        assertThat(responseEntity.getBody().get(0).get("price").get("currency").textValue()).isEqualTo("zł");
        assertThat(responseEntity.getBody().get(0).get("price").get("period").textValue()).isEqualTo("miesiąc");
        assertThat(responseEntity.getBody().get(0).get("isPremium").booleanValue()).isFalse();
        assertThat(responseEntity.getBody().get(0).get("description").get("synopsis").textValue())
                .isEqualTo("Karnet uprawniający do korzystania w pełni z usług ośrodka");
        assertThat(responseEntity.getBody().get(0).get("description").get("features").get(0).textValue())
                .isEqualTo("Full pakiet");
        assertThat(responseEntity.getBody().get(0).get("description").get("features").get(1).textValue())
                .isEqualTo("sauna");
        assertThat(responseEntity.getBody().get(0).get("description").get("features").get(2).textValue())
                .isEqualTo("siłownia");
        assertThat(responseEntity.getBody().get(0).get("description").get("features").get(3).textValue())
                .isEqualTo("basen");

        assertThat(responseEntity.getBody().get(1).get("title").textValue()).isEqualTo("Karnet kwartalny");
        assertThat(responseEntity.getBody().get(1).get("subheader").textValue())
                .isEqualTo("Najlepszy wybór dla osób aktywnych i korzystny cenowo");
        assertThat(responseEntity.getBody().get(1).get("price").get("amount").asDouble()).isEqualTo(399.99);
        assertThat(responseEntity.getBody().get(1).get("price").get("currency").textValue()).isEqualTo("zł");
        assertThat(responseEntity.getBody().get(1).get("price").get("period").textValue()).isEqualTo("miesiąc");
        assertThat(responseEntity.getBody().get(1).get("isPremium").booleanValue()).isFalse();
        assertThat(responseEntity.getBody().get(1).get("description").get("synopsis").textValue())
                .isEqualTo("Karnet uprawniający do korzystania w pełni z usług ośrodka");
        assertThat(responseEntity.getBody().get(1).get("description").get("features").get(0).textValue())
                .isEqualTo("Full pakiet");
        assertThat(responseEntity.getBody().get(1).get("description").get("features").get(1).textValue())
                .isEqualTo("sauna");
        assertThat(responseEntity.getBody().get(1).get("description").get("features").get(2).textValue())
                .isEqualTo("siłownia");
        assertThat(responseEntity.getBody().get(1).get("description").get("features").get(3).textValue())
                .isEqualTo("basen");
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        List<GymPassDocument> gymPassDocumentList = mongoTemplate.findAll(GymPassDocument.class);
        assertThat(gymPassDocumentList.size()).isEqualTo(2);
    }


}
