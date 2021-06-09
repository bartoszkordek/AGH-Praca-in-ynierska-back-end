package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainingTypeDAO extends MongoRepository<TrainingTypeDocument, String> {

    // TODO findAll
    // List<TrainingTypeDocument> findAll();

    TrainingTypeDocument findByTrainingTypeId(String trainingTypeId);

    TrainingTypeDocument findByName(String trainingName);

    boolean existsTrainingTypeById(String id);

    boolean existsByName(String trainingName);

    void deleteByTrainingTypeId(String trainingTypeId);

    void removeByName(String trainingName);
}
