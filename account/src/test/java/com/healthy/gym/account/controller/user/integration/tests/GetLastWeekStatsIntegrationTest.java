package com.healthy.gym.account.controller.user.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.FixedClockConfig;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FixedClockConfig.class)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetLastWeekStatsIntegrationTest {
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
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestDocumentUtilComponent utilComponent;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private Clock clock;

    private String adminToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }


    @BeforeEach
    void setUp() {
        adminToken = tokenFactory.getAdminToken();

        LocalDateTime now = LocalDateTime.now(clock);

        for (int i = 0; i < 7; i++) {
            utilComponent.saveAndGetTestUser(now.minusDays(i));
            if (i % 2 == 0) utilComponent.saveAndGetTestUser(now.minusDays(i));
        }
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
    }

    @Test
    void shouldReturnOnlyTrainers() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/admin/stats");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Authorization", adminToken);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();

        testEntity(body, 0, "2020-09-25", 2);
        testEntity(body, 1, "2020-09-26", 1);
        testEntity(body, 2, "2020-09-27", 2);
        testEntity(body, 3, "2020-09-28", 1);
        testEntity(body, 4, "2020-09-29", 2);
        testEntity(body, 5, "2020-09-30", 1);
        testEntity(body, 6, "2020-10-01", 2);
    }

    private void testEntity(JsonNode body, int number, String date, int quantity) {
        assertThat(body.get(number).get("quantity").intValue()).isEqualTo(quantity);
        assertThat(body.get(number).get("day").textValue()).isEqualTo(date);
    }
}
