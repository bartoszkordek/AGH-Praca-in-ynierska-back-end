package com.healthy.gym.trainings.service.reviewService;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import com.healthy.gym.trainings.service.ReviewServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UpdateReviewServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void shouldUpdateReview_whenValidRequestAndReviewIdAndClientId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);


        //when
        when(reviewRepository.findGroupTrainingsReviewsById(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(true);

        //then
        assertThat(reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId))
                .isEqualTo(response);
    }

    @Test(expected = NotExistingGroupTrainingReviewException.class)
    public void shouldNotUpdateReview_whenInvalidReviewId() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsById(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(false);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }

    @Test(expected = NotAuthorizedClientException.class)
    public void shouldNotUpdateReview_whenClientIsNotOwnerOfReview() throws StarsOutOfRangeException, NotAuthorizedClientException, NotExistingGroupTrainingReviewException {
        //mocks
        ReviewDAO reviewRepository = Mockito.mock(ReviewDAO.class);
        TrainingTypeDAO trainingTypeRepository = Mockito.mock(TrainingTypeDAO.class);
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, trainingTypeRepository);

        //before
        String reviewId = "852ed953-e37f-435a-bd1e-9fb2a327c4d5";
        String clientId = "client123";
        String trainingName = "TestTrainingName";
        String date = "2021-01-01";
        int starsBeforeUpdate = 5;
        String textBeforeUpdate = "Very good training!";
        int starsAfterUpdate = 4;
        String textAfterUpdate = "Good training!";

        GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel = new GroupTrainingReviewUpdateRequest(
                starsAfterUpdate, textAfterUpdate);

        GroupTrainingsReviews existingGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsBeforeUpdate, textBeforeUpdate);
        GroupTrainingsReviews updatedGroupTrainingsReview = new GroupTrainingsReviews(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);
        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewId, trainingName,
                clientId, date, starsAfterUpdate, textAfterUpdate);

        //when
        when(reviewRepository.findGroupTrainingsReviewsById(reviewId))
                .thenReturn(existingGroupTrainingsReview);
        when(reviewRepository.save(existingGroupTrainingsReview))
                .thenReturn(updatedGroupTrainingsReview);
        when(reviewRepository.existsByReviewId(reviewId))
                .thenReturn(true);
        when(reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId))
                .thenReturn(false);

        //then
        reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId,
                clientId);
    }

}
