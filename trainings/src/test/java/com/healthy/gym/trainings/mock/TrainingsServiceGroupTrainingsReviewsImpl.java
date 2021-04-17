package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.GroupTrainingReviewsDbRepository;
import com.healthy.gym.trainings.db.GroupTrainingsDbRepository;
import com.healthy.gym.trainings.db.TestRepository;
import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
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
}
