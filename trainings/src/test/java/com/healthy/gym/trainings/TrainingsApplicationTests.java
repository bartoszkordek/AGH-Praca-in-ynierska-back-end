package com.healthy.gym.trainings;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainingsApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Disabled("Error on github actions")
    @Test
    void shouldShowGroupTrainingsWhenValidRequest(){
        // when
        final ResponseEntity<Void> response = restTemplate.exchange(
                "/trainings/group", HttpMethod.GET,
                new HttpEntity<>(null, null), Void.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
