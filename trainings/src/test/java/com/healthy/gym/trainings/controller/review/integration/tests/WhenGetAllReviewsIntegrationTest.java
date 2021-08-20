package com.healthy.gym.trainings.controller.review.integration.tests;

import com.healthy.gym.trainings.component.TokenManager;
import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.fetch-registry=false",
        "eureka.client.register-with-eureka=false"
})
@ActiveProfiles(value = "test")
@Tag("integration")
class WhenGetAllReviewsIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    private MongoTemplate mongoTemplate;


    private String userToken;
    private String adminToken;
    private String userId;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();

        userToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(userId)
                .claim("roles", List.of("ROLE_USER"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();

        adminToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(adminId)
                .claim("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(GroupTrainingsReviews.class);
    }


    @Nested
    class ShouldGetAllReviews{

        @Test
        void test() throws URISyntaxException {
            assertThat(true).isTrue();
        }
    }

    @Nested
    class ShouldNotGetAllReviews{

        @Test
        void whenNotAuthorized() throws URISyntaxException {
            int pageNo = 0;
            String startDate = "2021-06-10";
            String endDate = "2021-06-20";
            URI uri = new URI("http://localhost:" + port + "/trainings/review/page/"+
                    pageNo+
                    "?"+"startDate="+startDate+
                    "&"+"endDate="+endDate);
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Map<String, Object>>(){});

            AssertionsForClassTypes.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        }

    }


}
