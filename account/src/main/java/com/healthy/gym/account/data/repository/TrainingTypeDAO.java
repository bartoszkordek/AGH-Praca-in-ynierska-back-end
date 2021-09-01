package com.healthy.gym.account.data.repository;

import com.healthy.gym.account.data.document.TrainingTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainingTypeDAO extends MongoRepository<TrainingTypeDocument, String> {

    TrainingTypeDocument findByTrainingTypeId(String trainingTypeId);

    TrainingTypeDocument findByName(String trainingName);

    boolean existsByTrainingTypeId(String id);

    boolean existsByName(String trainingName);

    void deleteByTrainingTypeId(String trainingTypeId);

    void removeByName(String trainingName);
}
