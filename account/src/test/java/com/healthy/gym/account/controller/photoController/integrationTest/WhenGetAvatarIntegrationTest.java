package com.healthy.gym.account.controller.photoController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.pojo.Image;
import org.bson.types.Binary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
class WhenGetAvatarIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userToken;
    private String userId;
    private byte[] imageBytes;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws IOException {
        userId = UUID.randomUUID().toString();

        userToken = tokenFactory.getUserToken(userId);

        Resource resource = new ClassPathResource("mem.jpg");
        Path filePath = resource.getFile().toPath();
        imageBytes = Files.readAllBytes(filePath);
        Binary image = new Binary(imageBytes);
        PhotoDocument avatar = new PhotoDocument(userId, "title", new Image(image, MediaType.IMAGE_JPEG_VALUE));

        mongoTemplate.save(avatar);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(PhotoDocument.class);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldReturnAvatar(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/photos/" + userId + "/avatar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("avatar.get.found");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(responseEntity.getBody().get("avatar").get("format").textValue())
                .isEqualTo(MediaType.IMAGE_JPEG_VALUE);
        assertThat(responseEntity.getBody().get("avatar").get("data").textValue()).isEqualTo(imageBase64);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowExceptionWhenAvatarNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/photos/" + UUID.randomUUID() + "/avatar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        String expectedMessage = messages.get("avatar.not.found.exception");

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).hasToString(MediaType.APPLICATION_JSON_VALUE);
        assertThat(responseEntity.getBody().get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseEntity.getBody().get("error").textValue()).isEqualTo("Not Found");
        assertThat(responseEntity.getBody().get("status").numberValue()).isEqualTo(404);
        assertThat(responseEntity.getBody().get("timestamp")).isNotNull();
    }
}
