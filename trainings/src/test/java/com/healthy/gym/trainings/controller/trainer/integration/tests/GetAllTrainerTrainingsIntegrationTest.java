package com.healthy.gym.trainings.controller.trainer.integration.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetAllTrainerTrainingsIntegrationTest {

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
    private TestDocumentUtilComponent utilComponent;
    @Autowired
    private MongoTemplate mongoTemplate;
    private String trainerToken;
    private String userId;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        trainerToken = tokenFactory.getTrainerToken(userId);
        var trainer = utilComponent.saveAndGetTestTrainer(userId);

        utilComponent.saveAndGetTestIndividualTraining(
                "2020-10-01T10:00",
                "2020-10-01T11:00",
                List.of(trainer)
        );

        utilComponent.saveAndGetTestIndividualTraining(
                "2020-10-10T10:00",
                "2020-10-10T11:00",
                List.of(trainer)
        );

        utilComponent.saveAndGetTestIndividualTraining(
                "2020-10-09T10:00",
                "2020-10-09T11:00",
                trainer
        );

        utilComponent.saveAndGetTestGroupTraining(
                "2020-10-10T09:00",
                "2020-10-10T09:30",
                List.of(trainer)
        );
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(GroupTrainingDocument.class);
    }

    @Test
    void shouldCreateNewLocation() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/trainer/" + userId +
                "/trainings?startDate=2020-10-05&&endDate=2020-10-12"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", TestCountry.ENGLAND.toString());
        headers.set("Authorization", trainerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, request, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isEqualTo(3);
        System.out.println(body);
    }
}
