package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.model.request.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.request.GroupTrainingsReviewsUpdateModel;
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

    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return groupTrainingReviewsRepository.findAll();
    }

    public GroupTrainingsReviews getGroupTrainingsReviewById(String reviewId){
        return groupTrainingReviewsRepository.findGroupTrainingsReviewsById(reviewId);
    }

    public boolean isGroupTrainingsReviewExist(String reviewId){
        return groupTrainingReviewsRepository.existsById(reviewId);
    }

    public boolean isClientReviewOwner(String reviewId, String clientId){
        return groupTrainingReviewsRepository.existsByIdAndAndClientId(reviewId, clientId);
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
        GroupTrainingsReviews groupTrainingsReviews = groupTrainingReviewsRepository.findGroupTrainingsReviewsById(reviewId);
        groupTrainingReviewsRepository.removeById(reviewId);
        return groupTrainingsReviews;
    }

    public GroupTrainingsReviews updateGroupTrainingsReview(GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel,
                                                            String reviewId){
        GroupTrainingsReviews groupTrainingsReview = groupTrainingReviewsRepository.findGroupTrainingsReviewsById(reviewId);
        int stars = groupTrainingsReviewsUpdateModel.getStars();
        String text = groupTrainingsReviewsUpdateModel.getText();
        if(stars >= 1 && stars <=5){
            groupTrainingsReview.setStars(groupTrainingsReviewsUpdateModel.getStars());
        }
        if(!text.isEmpty()){
            groupTrainingsReview.setText(groupTrainingsReviewsUpdateModel.getText());
        }
        GroupTrainingsReviews response = groupTrainingReviewsRepository.save(groupTrainingsReview);
        return response;
    }
}
