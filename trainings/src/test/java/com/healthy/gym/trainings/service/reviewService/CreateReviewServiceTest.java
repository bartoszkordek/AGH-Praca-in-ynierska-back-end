package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CreateReviewServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldCreateReview_whenValidRequest() throws StarsOutOfRangeException, TrainingTypeNotFoundException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        int stars = 5;
        String text = "Good training";
        GroupTrainingReviewRequest groupTrainingsReviewsModel= new GroupTrainingReviewRequest(trainingTypeId, stars, text);
        String clientId = "client123";

        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        GroupTrainingsReviews dbResponse = new GroupTrainingsReviews(reviewId, trainingTypeId, clientId,
                todayDateFormatted, stars, text);
        dbResponse.setId("507f1f77bcf86cd799439011");

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(
                dbResponse.getReviewId(),
                dbResponse.getTrainingTypeId(),
                dbResponse.getClientId(),
                dbResponse.getDate(),
                dbResponse.getStars(),
                dbResponse.getText());

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId))
                .thenReturn(true);
        when(reviewRepository.insert( new GroupTrainingsReviews(
                reviewId,
                groupTrainingsReviewsModel.geTrainingTypeId(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        ))).thenReturn(dbResponse);

        //then
        assertThat(reviewService.createGroupTrainingReview(groupTrainingsReviewsModel, clientId))
                .isEqualTo(response);
    }

    @Test(expected = TrainingTypeNotFoundException.class)
    public void shouldNotCreateReview_whenTrainingTypeIdNotExist() throws StarsOutOfRangeException, TrainingTypeNotFoundException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        int stars = 5;
        String text = "Good training";
        GroupTrainingReviewRequest groupTrainingsReviewsModel= new GroupTrainingReviewRequest(trainingTypeId, stars, text);
        String clientId = "client123";

        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        GroupTrainingsReviews dbResponse = new GroupTrainingsReviews(reviewId, trainingTypeId, clientId,
                todayDateFormatted, stars, text);
        dbResponse.setId("507f1f77bcf86cd799439011");

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(
                dbResponse.getReviewId(),
                dbResponse.getTrainingTypeId(),
                dbResponse.getClientId(),
                dbResponse.getDate(),
                dbResponse.getStars(),
                dbResponse.getText());

        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId))
                .thenReturn(false);
        when(reviewRepository.insert( new GroupTrainingsReviews(
                reviewId,
                groupTrainingsReviewsModel.geTrainingTypeId(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        ))).thenReturn(dbResponse);

        //then
        assertThat(reviewService.createGroupTrainingReview(groupTrainingsReviewsModel, clientId))
                .isEqualTo(response);
    }

    @Test(expected = StarsOutOfRangeException.class)
    public void shouldNotCreateReview_whenStarsLessThan1() throws StarsOutOfRangeException, TrainingTypeNotFoundException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        int stars = 0;
        String text = "Total zero!";
        GroupTrainingReviewRequest groupTrainingsReviewsModel= new GroupTrainingReviewRequest(trainingTypeId, stars, text);
        String clientId = "client123";

        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        GroupTrainingsReviews dbResponse = new GroupTrainingsReviews(reviewId, trainingTypeId, clientId,
                todayDateFormatted, stars, text);
        dbResponse.setId("507f1f77bcf86cd799439011");

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(
                dbResponse.getReviewId(),
                dbResponse.getTrainingTypeId(),
                dbResponse.getClientId(),
                dbResponse.getDate(),
                dbResponse.getStars(),
                dbResponse.getText());

        GroupTrainingsReviews groupTrainingsReviews = new GroupTrainingsReviews(
                reviewId,
                groupTrainingsReviewsModel.geTrainingTypeId(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        );
        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId))
                .thenReturn(true);
        when(reviewRepository.insert(groupTrainingsReviews)).thenReturn(dbResponse);

        //then
        reviewService.createGroupTrainingReview(groupTrainingsReviewsModel, clientId);
    }


    @Test(expected = StarsOutOfRangeException.class)
    public void shouldNotCreateReview_whenStarsGreaterThan5() throws StarsOutOfRangeException, TrainingTypeNotFoundException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String trainingTypeId = "eeded953-e37f-435a-bd1e-9fb2a327c46m";
        int stars = 6;
        String text = "Ultra good training";
        GroupTrainingReviewRequest groupTrainingsReviewsModel= new GroupTrainingReviewRequest(trainingTypeId, stars, text);
        String clientId = "client123";

        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        GroupTrainingsReviews dbResponse = new GroupTrainingsReviews(reviewId, trainingTypeId, clientId,
                todayDateFormatted, stars, text);
        dbResponse.setId("507f1f77bcf86cd799439011");

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(
                dbResponse.getReviewId(),
                dbResponse.getTrainingTypeId(),
                dbResponse.getClientId(),
                dbResponse.getDate(),
                dbResponse.getStars(),
                dbResponse.getText());

        GroupTrainingsReviews groupTrainingsReviews = new GroupTrainingsReviews(
                reviewId,
                groupTrainingsReviewsModel.geTrainingTypeId(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        );
        //when
        when(trainingTypeRepository.existsByTrainingTypeId(trainingTypeId))
                .thenReturn(true);
        when(reviewRepository.insert(groupTrainingsReviews)).thenReturn(dbResponse);

        //then
        reviewService.createGroupTrainingReview(groupTrainingsReviewsModel, clientId);
    }
}
