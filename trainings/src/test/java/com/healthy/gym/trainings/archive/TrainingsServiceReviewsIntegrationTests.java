package com.healthy.gym.trainings.archive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@Disabled
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(properties = {
//        "eureka.client.fetch-registry=false",
//        "eureka.client.register-with-eureka=false"
//})
public class TrainingsServiceReviewsIntegrationTests {
//    @Container
//    static MongoDBContainer mongoDBContainer =
//            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
//
//    @LocalServerPort
//    private Integer port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
//    }
//
//    @Test
//    public void contextLoads() {
//    }
//
//    @Test
//    public void whenRequestHasEveryFieldValid() throws URISyntaxException, JsonProcessingException {
//
//        URI uri = new URI("http://localhost:8020/trainings/reviews/all");
//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);
//
//        ResponseEntity<JsonNode> responseEntity = restTemplate
//                .exchange(uri, HttpMethod.GET, requestEntity, JsonNode.class);
//
//        System.out.println(responseEntity.getStatusCode());
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
}

