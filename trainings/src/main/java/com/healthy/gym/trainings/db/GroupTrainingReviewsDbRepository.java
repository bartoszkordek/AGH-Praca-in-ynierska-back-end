package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
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
}
