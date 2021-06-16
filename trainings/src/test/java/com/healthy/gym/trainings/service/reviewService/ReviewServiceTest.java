package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import com.healthy.gym.trainings.service.email.EmailService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
public class ReviewServiceTest {

    @TestConfiguration
    static class TrainingsReviewServiceImplTestContextConfiguration {

        @Bean
        @Primary
        public ReviewService reviewService() {
            return new ReviewServiceImpl(reviewDAO());
        }

        @Bean
        public EmailConfig emailConfig() {
            return new EmailConfig();
        }

        @Bean
        public ReviewDAO reviewDAO() {
            return Mockito.mock(ReviewDAO.class);
        }

    }

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ReviewDAO reviewRepository;


    @BeforeEach
    void setUp() {

    }

    @Test
    public void shouldReturnFirstReview_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
        //before
        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        //populated both start and end date
        //when
        String startDateMinusOneDayFormatted = "2020-12-31";
        String endDatePlusOneDayFormatted = "2021-02-02";

        when(reviewRepository.findByDateBetween(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDayFormatted,
                        endDatePlusOneDayFormatted, pageable));


        //populated start date only
        //when
        String defaultEndDatePlusOneDayFormatted = "2100-01-01";
        when(reviewRepository.findByDateBetween(startDateMinusOneDayFormatted,
                defaultEndDatePlusOneDayFormatted, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(startDate, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDayFormatted,
                        defaultEndDatePlusOneDayFormatted, pageable));

        //populated start date only
        //when
        String defaultStartDateMinusOneDayFormatted = "1899-12-31";
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(null, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDayFormatted,
                        endDatePlusOneDayFormatted, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDayFormatted,
                defaultEndDatePlusOneDayFormatted, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(null, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDayFormatted,
                        defaultEndDatePlusOneDayFormatted, pageable));
    }
}