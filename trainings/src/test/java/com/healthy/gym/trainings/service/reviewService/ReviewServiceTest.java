package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.InvalidUserIdException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
    @ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ReviewDAO.class))
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
    public void shouldReturnAllReviews_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
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

        //populated end date only
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

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviewsWhenStartDateAfterEndDate() throws ParseException, StartDateAfterEndDateException {
        //before
        String startDate = "2021-01-02";
        String endDate = "2021-01-01";
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

        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable));
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

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                endDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);

        //then
        assertThat(reviewService.getAllReviewsByUserId(null, endDate, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, validClientId, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByUserId(startDate, null, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, validClientId, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, validClientId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByUserId(null, null, validClientId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndClientId(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, validClientId, pageable));


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

    @Test
    public void shouldReturnAllReviewsByTrainingTypeId_whenValidRequest() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
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
        when(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                endDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, endDate, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, trainingName, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, null, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingName, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, null, trainingName, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingName(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingName, pageable));
    }
}