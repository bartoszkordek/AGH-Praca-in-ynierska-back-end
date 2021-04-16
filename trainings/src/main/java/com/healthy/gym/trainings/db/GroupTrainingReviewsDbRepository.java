package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupTrainingReviewsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private GroupTrainingReviewsRepository groupTrainingReviewsRepository;

    public List<GroupTrainingsReviews> getGroupTrainingReviewsRepository(){
        return groupTrainingReviewsRepository.findAll();
    }

    public GroupTrainingsReviews getGroupTrainingsReviewById(String reviewId){
        return groupTrainingReviewsRepository.findFirstBy(reviewId);
    }

    public boolean isGroupTrainingsReviewExist(String reviewId){
        return groupTrainingReviewsRepository.existsById(reviewId);
    }

    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingsReviewsModel groupTrainingsReviewsModel,
                                                           String date,
                                                           String clientId){

        GroupTrainingsReviews response = groupTrainingReviewsRepository.insert(new GroupTrainingsReviews(
                groupTrainingsReviewsModel.getTrainingName(),
                clientId,
                date,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        ));
        return response;
    }

    public GroupTrainingsReviews removeGroupTrainingsReview(String reviewId){
        GroupTrainingsReviews groupTrainingsReviews = groupTrainingReviewsRepository.findFirstBy(reviewId);
        groupTrainingReviewsRepository.removeById(reviewId);
        return groupTrainingsReviews;
    }
}
