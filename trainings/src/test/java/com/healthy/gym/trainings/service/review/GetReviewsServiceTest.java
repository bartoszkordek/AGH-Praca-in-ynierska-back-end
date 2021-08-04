package com.healthy.gym.trainings.service.review;

import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.invalid.InvalidUserIdException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GetReviewsServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldReturnAllReviews_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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

    @Test
    public void shouldReturnAllReviewsInTimeFrame_whenValidRequest() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviewsAll = new ArrayList<>();
        List<GroupTrainingReviewResponse> reviewsBeforeJuly2021 = new ArrayList<>();
        List<GroupTrainingReviewResponse> reviewsAfterJuly2021 = new ArrayList<>();

        String reviewIdRev1 = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeIdRev1 = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String clientIdRev1 = "Client123";
        String dateRev1 = "2021-01-01";
        int starsRev1 = 5;
        String textRev1 = "Very good training!";
        GroupTrainingReviewResponse review1 = new GroupTrainingReviewResponse(
                reviewIdRev1,
                trainingTypeIdRev1,
                clientIdRev1,
                dateRev1,
                starsRev1,
                textRev1);
        reviewsAll.add(review1);
        reviewsBeforeJuly2021.add(review1);

        String reviewIdRev2 = "852ed953-e37f-435a-bd1e-9fb2a327c4d6";
        String trainingTypeIdRev2 = "abded953-e37f-435a-bd1e-9fb2a327c46k";
        String clientIdRev2 = "Client123";
        String dateRev2 = "2021-08-01";
        int starsRev2 = 4;
        String textRev2 = "Good training!";
        GroupTrainingReviewResponse review2 = new GroupTrainingReviewResponse(
                reviewIdRev2,
                trainingTypeIdRev2,
                clientIdRev2,
                dateRev2,
                starsRev2,
                textRev2);
        reviewsAll.add(review2);
        reviewsAfterJuly2021.add(review2);

        Page<GroupTrainingReviewResponse> reviewsBeforeJuly2021InPages = new PageImpl<>(reviewsBeforeJuly2021);
        Page<GroupTrainingReviewResponse> reviewsAfterJuly2021InPages = new PageImpl<>(reviewsAfterJuly2021);

        //populated both start and end date before July 2021 (between 2021-01-01 and 2021-07-01)
        String beforeJuly2021PeriodStartDate = "2021-01-01";
        String beforeJuly2021PeriodEndDate = "2021-07-31";

        //when
        String beforeJuly2021PeriodStartDateMinusOneDay = "2020-12-31";
        String beforeJuly2021PeriodEndDatePlusOneDay = "2021-08-01";
        when(reviewRepository.findByDateBetween(beforeJuly2021PeriodStartDateMinusOneDay,
                beforeJuly2021PeriodEndDatePlusOneDay, pageable))
                .thenReturn(reviewsBeforeJuly2021InPages);
        //then
        assertThat(reviewService.getAllReviews(beforeJuly2021PeriodStartDate, beforeJuly2021PeriodEndDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(beforeJuly2021PeriodStartDateMinusOneDay,
                        beforeJuly2021PeriodEndDatePlusOneDay, pageable));


        //populated both start and end date after July 2021 (between 2021-08-01 and 2021-31-01)
        String afterJuly2021PeriodStartDate = "2021-08-01";
        String afterJuly2021PeriodEndDate = "2021-12-31";

        //when
        String afterJuly2021PeriodStartDateMinusOneDay = "2021-07-31";
        String afterJuly2021PeriodEndDatePlusOneDay = "2022-01-01";
        when(reviewRepository.findByDateBetween(afterJuly2021PeriodStartDateMinusOneDay,
                afterJuly2021PeriodEndDatePlusOneDay, pageable))
                .thenReturn(reviewsBeforeJuly2021InPages);

        //then
        assertThat(reviewService.getAllReviews(afterJuly2021PeriodStartDate, afterJuly2021PeriodEndDate, pageable))
                .isEqualTo(reviewRepository.findByDateBetween(afterJuly2021PeriodStartDateMinusOneDay,
                        afterJuly2021PeriodEndDatePlusOneDay, pageable));
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviews_whenStartDateAfterEndDate() throws ParseException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String startDate = "2021-01-02";
        String endDate = "2021-01-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviewsByUserId_whenStartDateAfterEndDate() throws ParseException, StartDateAfterEndDateException, InvalidUserIdException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByUserId(startDate, endDate, validClientId, pageable);
    }

    @Test(expected = InvalidUserIdException.class)
    public void shouldNotReturnAllReviewsByUserId_whenEmptyUserId() throws ParseException, InvalidUserIdException, StartDateAfterEndDateException, InvalidUserIdException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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

        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                endDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                        endDatePlusOneDay, trainingTypeId, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                endDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, endDate, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, trainingTypeId, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(startDate, null, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingTypeId, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeId(null, null, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingTypeId, pageable));
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllReviewsByTrainingTypeId_whenStartDateAfterEndDate() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingTypeId, pageable);
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnAllReviewsByTrainingTypeId_whenEmptyTrainingTypeId() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
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
        when(reviewRepository.findByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay, defaultEndDatePlusOneDay,
                trainingTypeId, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByTrainingTypeId(defaultStartDateMinusOneDay, defaultEndDatePlusOneDay,
                null, pageable);

    }


    @Test
    public void shouldReturnAllPublicReviewsByTrainingTypeId_whenValidRequest() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingTypeId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                endDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, endDate, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                        endDatePlusOneDay, trainingTypeId, pageable));

        //populated end date only
        //when
        String defaultStartDateMinusOneDay = "1899-12-31";
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                endDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(null, endDate, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                        endDatePlusOneDay, trainingTypeId, pageable));

        //populated start date only
        //when
        String defaultEndDatePlusOneDay = "2100-01-01";
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, null, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingTypeId, pageable));

        //not populated start and end date
        //when
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);
        //then
        assertThat(reviewService.getAllReviewsByTrainingTypeIdPublic(null, null, trainingTypeId, pageable))
                .isEqualTo(reviewRepository.getAllByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                        defaultEndDatePlusOneDay, trainingTypeId, pageable));
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotReturnAllPublicReviewsByTrainingTypeId_whenEmptyTrainingTypeId() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingTypeId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        String startDate = "2021-01-01";
        String endDate = "2021-02-01";
        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        //populated both start and end date
        //when
        String startDateMinusOneDay = "2020-12-31";
        String endDatePlusOneDay = "2021-02-02";
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(startDateMinusOneDay,
                endDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);

        //then
        reviewService.getAllReviewsByTrainingTypeIdPublic(startDate, endDate, trainingTypeId, pageable);
    }

    @Test(expected = StartDateAfterEndDateException.class)
    public void shouldNotReturnAllPublicReviewsByTrainingTypeId_whenStartDateAfterEndDate() throws ParseException, TrainingTypeNotFoundException, StartDateAfterEndDateException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewPublicResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewPublicResponse review = new GroupTrainingReviewPublicResponse(
                reviewId,
                trainingTypeId,
                date,
                stars,
                text);
        reviews.add(review);

        Page<GroupTrainingReviewPublicResponse> reviewsInPages = new PageImpl<>(reviews);

        int page = 0;
        int size = 15;
        Pageable pageable = PageRequest.of(page, size);

        String defaultStartDateMinusOneDay = "1899-12-31";
        String defaultEndDatePlusOneDay = "2100-01-01";

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)).thenReturn(true);
        when(reviewRepository.getAllByDateBetweenAndTrainingTypeId(defaultStartDateMinusOneDay,
                defaultEndDatePlusOneDay, trainingTypeId, pageable))
                .thenReturn(reviewsInPages);

        String startDate = "2021-31-12";
        String endDate = "2021-01-01";

        //then
        reviewService.getAllReviewsByTrainingTypeId(startDate, endDate, trainingTypeId, pageable);
    }

    @Test
    public void shouldReturnReview_whenValidReviewId() throws NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        //when
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.getFirstByReviewId(reviewId))
                .thenReturn(reviews.get(0));

        //then
        assertThat(reviewService.getReviewByReviewId(reviewId))
            .isEqualTo(reviews.get(0));
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldNotReturnReview_whenInvalidReviewId() throws NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        List<GroupTrainingReviewResponse> reviews = new ArrayList<>();
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d7";
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        String validClientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";
        GroupTrainingReviewResponse review = new GroupTrainingReviewResponse(
                reviewId,
                trainingTypeId,
                validClientId,
                date,
                stars,
                text);
        reviews.add(review);

        //when
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(false);
        when(reviewRepository.getFirstByReviewId(reviewId))
                .thenReturn(reviews.get(0));

        //then
        reviewService.getReviewByReviewId(reviewId);
    }


}
