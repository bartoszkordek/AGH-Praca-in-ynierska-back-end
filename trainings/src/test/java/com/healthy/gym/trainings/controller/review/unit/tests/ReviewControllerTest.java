package com.healthy.gym.trainings.controller.review.unit.tests;

import com.healthy.gym.trainings.controller.ReviewController;
import com.healthy.gym.trainings.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        void shouldNotGetReviews_whenNotAuthorized() throws Exception {
            assertThat(true).isTrue();

            RequestBuilder request = MockMvcRequestBuilders
                    .put(uri);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}
