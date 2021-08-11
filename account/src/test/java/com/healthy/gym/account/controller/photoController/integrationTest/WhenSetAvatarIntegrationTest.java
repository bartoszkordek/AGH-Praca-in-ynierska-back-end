package com.healthy.gym.account.controller.photoController.integrationTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.healthy.gym.account.component.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.data.document.PhotoDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.pojo.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@Tag("integration")
class WhenSetAvatarIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private TestRoleTokenFactory tokenFactory;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private Environment environment;

    private String userToken;
    private String userId;
    private Resource currentImageResource;
    private Resource updatedImageResource;

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

        currentImageResource = new ClassPathResource("testImagess/shiba_inu_smile_1.jpg");
        updatedImageResource = new ClassPathResource("testImagess/shiba_inu_smile_2.jpg");

        PhotoDocument avatar = new PhotoDocument(
                userId,
                currentImageResource.getFilename(),
                new Image(getImageBytes(currentImageResource), MediaType.IMAGE_JPEG_VALUE)
        );

        UserDocument userDocument = new UserDocument(
                "testName",
                "testSurname",
                "testEmail",
                "testPhone",
                "encryptedPassword",
                userId
        );

        mongoTemplate.save(userDocument);
        mongoTemplate.save(avatar);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(UserDocument.class);
        mongoTemplate.dropCollection(PhotoDocument.class);
    }

    private byte[] getImageBytes(Resource imageResource) throws IOException {
        File imageFile = imageResource.getFile();
        FileInputStream inputStream = new FileInputStream(imageFile);
        return inputStream.readAllBytes();
    }

    private HttpEntity<Object> getImagePart() {
        HttpHeaders imageHeaders = new HttpHeaders();
        imageHeaders.setContentType(MediaType.IMAGE_JPEG);
        return new HttpEntity<>(updatedImageResource, imageHeaders);
    }

    private String getExpectedAvatarLocation(String userId) {
        String gateway = environment.getProperty("gateway");
        String microservice = environment.getProperty("spring.application.name");
        return gateway + "/" + microservice + "/photos/" + userId + "/avatar";
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldSetAvatar(TestCountry country) throws Exception {
        mongoTemplate.dropCollection(PhotoDocument.class);
        List<PhotoDocument> avatarList = mongoTemplate.findAll(PhotoDocument.class);
        assertThat(avatarList.size()).isZero();

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/photos/" + userId + "/avatar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("avatar", getImagePart());

        HttpEntity<Object> requestEntity = new HttpEntity<>(multipartRequest, headers);
        String expectedMessage = messages.get("avatar.update.success");
        String expectedAvatarLocation = getExpectedAvatarLocation(userId);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        HttpHeaders httpHeaders = responseEntity.getHeaders();
        assertThat(httpHeaders.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode responseBody = responseEntity.getBody();
        assertThat(responseBody.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseBody.get("avatar").textValue()).isEqualTo(expectedAvatarLocation);

        avatarList = mongoTemplate.findAll(PhotoDocument.class);
        assertThat(avatarList.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldUpdateAvatar(TestCountry country) throws Exception {
        List<PhotoDocument> avatarList = mongoTemplate.findAll(PhotoDocument.class);
        assertThat(avatarList.size()).isEqualTo(1);
        assertThat(avatarList.get(0).getImage().getData().getData())
                .isEqualTo(getImageBytes(currentImageResource));

        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("http://localhost:" + port + "/photos/" + userId + "/avatar");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", testedLocale.toString());
        headers.set("Authorization", userToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        multipartRequest.add("avatar", getImagePart());

        HttpEntity<Object> requestEntity = new HttpEntity<>(multipartRequest, headers);
        String expectedMessage = messages.get("avatar.update.success");
        String expectedAvatarLocation = getExpectedAvatarLocation(userId);

        ResponseEntity<JsonNode> responseEntity = restTemplate
                .exchange(uri, HttpMethod.POST, requestEntity, JsonNode.class);

        HttpStatus httpStatus = responseEntity.getStatusCode();
        assertThat(httpStatus).isEqualTo(HttpStatus.OK);

        HttpHeaders httpHeaders = responseEntity.getHeaders();
        assertThat(httpHeaders.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode responseBody = responseEntity.getBody();
        assertThat(responseBody.get("message").textValue()).isEqualTo(expectedMessage);
        assertThat(responseBody.get("avatar").textValue()).isEqualTo(expectedAvatarLocation);

        avatarList = mongoTemplate.findAll(PhotoDocument.class);
        assertThat(avatarList.size()).isEqualTo(1);
        assertThat(avatarList.get(0).getImage().getData().getData())
                .isEqualTo(getImageBytes(updatedImageResource));
    }

}
