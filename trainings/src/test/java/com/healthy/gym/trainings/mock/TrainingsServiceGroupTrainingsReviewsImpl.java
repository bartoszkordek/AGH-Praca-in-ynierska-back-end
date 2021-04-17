package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TrainingsServiceGroupTrainingsReviewsImpl extends TrainingsService {
    public TrainingsServiceGroupTrainingsReviewsImpl(TestRepository testRepository,
                                                     GroupTrainingsDbRepository groupTrainingsDbRepository,
                                                     GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository) {
        super(testRepository, groupTrainingsDbRepository, groupTrainingReviewsDbRepository);
    }

    @Autowired
    GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository;

    @Override
    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return groupTrainingReviewsDbRepository.getGroupTrainingReviews();
    }

    @Override
    public GroupTrainingsReviews getGroupTrainingReviewById(String reviewId) throws NotExistingGroupTrainingReviewException {
        if(!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Review with ID: "+ reviewId + " doesn't exist");
        }
        return groupTrainingReviewsDbRepository.getGroupTrainingsReviewById(reviewId);
    }

    @Override
    public GroupTrainingsReviews updateGroupTrainingReview(GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel,
                                                           String reviewId,
                                                           String clientId) throws NotAuthorizedClientException, StarsOutOfRangeException, NotExistingGroupTrainingReviewException {
        if(!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Review with ID: "+ reviewId + " doesn't exist");
        }
        if(!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)){
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        if(groupTrainingsReviewsUpdateModel.getStars()<1 || groupTrainingsReviewsUpdateModel.getStars() >5){
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        return groupTrainingReviewsDbRepository.updateGroupTrainingsReview(groupTrainingsReviewsUpdateModel,reviewId);
    }
}
