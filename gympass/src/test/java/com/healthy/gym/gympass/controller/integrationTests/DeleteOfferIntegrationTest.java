package com.healthy.gym.gympass.controller.integrationTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.gympass.configuration.FixedClockConfig;
import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.data.document.GymPassDocument;
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
public class DeleteOfferIntegrationTest {

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
    private String existingDocumentId;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


    @BeforeEach
    void setUp() {

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
    }

    @AfterEach
    void tearDown() { mongoTemplate.dropCollection(GymPassDocument.class); }

    @Nested
    class ShouldDeleteOffer{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldDeleteGymPassOfferWhenValidId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            URI uri = new URI("http://localhost:" + port + "/offer/"+existingDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("offer.removed");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getBody().get("gymPass").get("title").textValue())
                    .isEqualTo("Pojedyncze wejście");
            assertThat(responseEntity.getBody().get("gymPass").get("subheader").textValue())
                    .isEqualTo("Zapraszamy jeżeli chcesz sprawdzić jak wygląda nasza siłownia");
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("amount").asDouble())
                    .isEqualTo(19.99);
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("currency").textValue())
                    .isEqualTo("zł");
            assertThat(responseEntity.getBody().get("gymPass").get("price").get("period").textValue())
                    .isEqualTo("jednorazowy");
            assertThat(responseEntity.getBody().get("gymPass").get("isPremium").booleanValue()).isFalse();
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("synopsis").textValue())
                    .isEqualTo("Karnet uprawniający do jednorazowego skorzystania w pełni z usług ośrodka");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(0).textValue())
                    .isEqualTo("sauna");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(1).textValue())
                    .isEqualTo("siłownia");
            assertThat(responseEntity.getBody().get("gymPass").get("description").get("features").get(2).textValue())
                    .isEqualTo("basen");
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

            List<GymPassDocument> gymPassDocumentList = mongoTemplate.findAll(GymPassDocument.class);
            assertThat(gymPassDocumentList.size()).isZero();
        }
    }

    @Nested
    class ShouldNotDeleteOffer{

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInvalidGymPassOfferIdExceptionWhenInvalidId(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidDocumentId = UUID.randomUUID().toString();

            URI uri = new URI("http://localhost:" + port + "/offer/"+invalidDocumentId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", testedLocale.toString());
            headers.set("Authorization", managerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);


            HttpEntity<Object> request = new HttpEntity<>(null, headers);
            String expectedMessage = messages.get("exception.invalid.offer.id");

            ResponseEntity<JsonNode> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(Objects.requireNonNull(responseEntity.getBody().get("message").textValue()))
                    .isEqualTo(expectedMessage);
            assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        }

        @Nested
        class ShouldNotCreateOfferWhenNotAuthorized{

            @ParameterizedTest
            @EnumSource(TestCountry.class)
            void shouldNotGetOffersWhenNoToken(TestCountry country) throws Exception {
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/"+existingDocumentId);

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
            void shouldNotGetOffersWhenLoggedAsUser(TestCountry country) throws Exception {
                Map<String, String> messages = getMessagesAccordingToLocale(country);
                Locale testedLocale = convertEnumToLocale(country);

                URI uri = new URI("http://localhost:" + port + "/offer/"+existingDocumentId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept-Language", testedLocale.toString());
                headers.set("Authorization", userToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(null, headers);

                ResponseEntity<JsonNode> responseEntity = restTemplate
                        .exchange(uri, HttpMethod.DELETE, request, JsonNode.class);

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


