package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockBean
    private ReviewDAO reviewRepository;

    @MockBean
    private TrainingTypeDAO trainingTypeRepository;


    @BeforeEach
    void setUp() {

    }

//    @Test
//    public void test() throws ParseException, StartDateAfterEndDateException {
//        assertThat(true).isTrue();
//
//        reviewService.getAllReviews(null, null, null);
//    }

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
        reviewRepository.findAll();
        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
                endDatePlusOneDay, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviews(startDate, endDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
                        endDatePlusOneDay, pageable));


//        //populated start date only
//        //when
//        String defaultEndDatePlusOneDay = "2100-01-01";
//        when(reviewRepository.findByDateBetween(startDateMinusOneDay,
//                defaultEndDatePlusOneDay, pageable))
//                .thenReturn(reviewsInPages);
//        //then
//        assertThat(reviewService.getAllReviews(startDate, null, pageable))
//                .isEqualTo(reviewRepository.findByDateBetween(startDateMinusOneDay,
//                        defaultEndDatePlusOneDay, pageable));
//
//        //populated end date only
//        //when
//        String defaultStartDateMinusOneDay = "1899-12-31";
//        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
//                endDatePlusOneDay, pageable))
//                .thenReturn(reviewsInPages);
//        //then
//        assertThat(reviewService.getAllReviews(null, endDate, pageable))
//                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
//                        endDatePlusOneDay, pageable));
//
//        //not populated start and end date
//        //when
//        when(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
//                defaultEndDatePlusOneDay, pageable))
//                .thenReturn(reviewsInPages);
//        //then
//        assertThat(reviewService.getAllReviews(null, null, pageable))
//                .isEqualTo(reviewRepository.findByDateBetween(defaultStartDateMinusOneDay,
//                        defaultEndDatePlusOneDay, pageable));
    }
}
