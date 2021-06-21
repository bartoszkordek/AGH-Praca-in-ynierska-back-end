package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ReviewServiceTest {

    @TestConfiguration
    static class TrainingsReviewServiceImplTestContextConfiguration {

        @Bean
        @Primary
        public ReviewService reviewService() {
            return new ReviewServiceImpl(reviewRepository(), trainingTypeRepository());
        }

        @Bean
        @Primary
        public ReviewDAO reviewRepository() {
            return Mockito.mock(ReviewDAO.class);
        }

        @Bean
        @Primary
        public TrainingTypeDAO trainingTypeRepository() {
            return Mockito.mock(TrainingTypeDAO.class);
        }

    }

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private ReviewDAO reviewRepository;

    @MockBean
    private TrainingTypeDAO trainingTypeRepository;


    @BeforeEach
    void setUp() {

    }

    @Test
    public void test(){
        assertThat(true).isTrue();
    }
}