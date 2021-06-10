package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ReviewController.class)
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Nested
    class WhenGetAllReviewsIsCalled{

        private URI uri;

        @BeforeEach
        void setUp() throws URISyntaxException {
            uri = new URI("/review/page/0");
        }

        @Test
        void sampleTest() throws Exception {
            assertThat(true).isTrue();
        }
    }
}
