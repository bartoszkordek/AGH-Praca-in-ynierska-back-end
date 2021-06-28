package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class RemoveReviewServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldRemoveReview_whenValidRequest() throws NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";

        GroupTrainingsReviews reviewToRemove = new GroupTrainingsReviews(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text
        );
        GroupTrainingReviewResponse reviewResponse = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);

        //when
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(reviewToRemove);

        //then
        assertThat(reviewService.removeGroupTrainingReviewByReviewId(reviewId, clientId))
                .isEqualTo(reviewResponse);
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldNotRemoveReview_whenInvalidReviewId() throws NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";

        GroupTrainingsReviews reviewToRemove = new GroupTrainingsReviews(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text
        );
        GroupTrainingReviewResponse reviewResponse = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);

        //when
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(false);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(reviewToRemove);

        reviewService.removeGroupTrainingReviewByReviewId(reviewId, clientId);
    }

    @Test(expected = NotAuthorizedClientException.class)
    public void shouldNotRemoveReview_whenClientIsNotOwnerOfReview() throws NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String trainingName = "TestTrainingName";
        String clientId = "Client123";
        String date = "2021-01-01";
        int stars = 5;
        String text = "Very good training!";

        GroupTrainingsReviews reviewToRemove = new GroupTrainingsReviews(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text
        );
        GroupTrainingReviewResponse reviewResponse = new GroupTrainingReviewResponse(
                reviewId,
                trainingName,
                clientId,
                date,
                stars,
                text);

        //when
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);
        when(reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId))
                .thenReturn(reviewToRemove);

        reviewService.removeGroupTrainingReviewByReviewId(reviewId, clientId);
    }


}
