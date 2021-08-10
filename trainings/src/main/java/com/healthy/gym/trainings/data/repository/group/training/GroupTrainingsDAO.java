package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupTrainingsDAO extends MongoRepository<GroupTrainingDocument, String> {

    GroupTrainingDocument findFirstByGroupTrainingId(String trainingId);
}
