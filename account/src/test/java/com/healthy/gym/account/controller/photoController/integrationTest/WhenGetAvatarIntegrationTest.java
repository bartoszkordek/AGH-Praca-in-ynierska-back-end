package com.healthy.gym.account.controller.photoController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.pojo.Image;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.DigestUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenGetAvatarIntegrationTest {

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userId;
    private byte[] imageBytes;
    private String digest;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() throws IOException {
        userId = UUID.randomUUID().toString();

        Resource resource = new ClassPathResource("mem.jpg");
        Path filePath = resource.getFile().toPath();
        imageBytes = Files.readAllBytes(filePath);
        digest = DigestUtils.md5DigestAsHex(imageBytes);
        Binary image = new Binary(imageBytes);
        PhotoDocument avatar = new PhotoDocument(userId, "title", new Image(image, MediaType.IMAGE_JPEG_VALUE));

        mongoTemplate.save(avatar);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(PhotoDocument.class);
    }

    @Test
    void shouldAcceptRequestAndShouldReturnAvatar() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/photos/" + userId + "/avatar/" + digest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<JsonNode> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<byte[]> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, byte[].class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        byte[] returnData = responseEntity.getBody();
        assertThat(returnData).isEqualTo(imageBytes);

        HttpHeaders httpHeaders = responseEntity.getHeaders();
        assertThat(Objects.requireNonNull(httpHeaders.getContentType()).toString())
                .hasToString(MediaType.IMAGE_JPEG_VALUE);
        assertThat(httpHeaders.getETag()).isNotNull();
        assertThat(httpHeaders.getCacheControl()).isEqualTo("max-age=600, private");
    }

    @Test
    void shouldAcceptRequestAndShouldThrowExceptionWhenAvatarNotFound() throws Exception {
        URI uri = new URI("http://localhost:" + port + "/photos/" + UUID.randomUUID() + "/avatar/" + digest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);

        JsonNode body = responseEntity.getBody();
        assert body != null;

        assertThat(body.get("error").textValue()).isEqualTo("Not Found");
        assertThat(body.get("status").numberValue()).isEqualTo(404);
        assertThat(body.get("timestamp")).isNotNull();

        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("avatar.not.found.exception");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
