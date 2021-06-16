package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.exception.InvalidUserIdException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import com.healthy.gym.trainings.service.email.EmailService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@Disabled
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
    public void shouldReturnAllReviews_whenValidReques() throws ParseException, StartDateAfterEndDateException {
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
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
                endDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
                        endDatePlusOneDay, pageable));


        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
                defaultEndDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(startDate, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, pageable));

        //populated start date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                endDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviews(null, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(null, null, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, pageable));
    }


    @Test
    public void shouldReturnAllReviewsByUserId_whenValidRequest() throws ParseException, InvalidUserIdException, StartDateAfterEndDateException {
        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                endDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviewsByUserId(startDate, endDate, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                        endDatePlusOneDay, validClientId, pageable));

        //populated start date only
        //when
        String defaultStartDateMinusOneDayFormatted = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDayFormatted,
                endDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviewsByUserId(null, endDate, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDayFormatted,
                        endDatePlusOneDay, validClientId, pageable));

        //not populated start and end date
        //when
        String defaultEndDatePlusOneDayFormatted = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDayFormatted,
                defaultEndDatePlusOneDayFormatted, validClientId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByUserId(null, null, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDayFormatted,
                        defaultEndDatePlusOneDayFormatted, validClientId, pageable));


    }

    @Test(expected = InvalidUserIdException.class)
    public void shouldNotReturnAllReviewsByUserId_whenEmptyUserId() throws ParseException, InvalidUserIdException, StartDateAfterEndDateException, InvalidUserIdException {
        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByUserId(null, null, null, pageable);

    }
}