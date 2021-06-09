package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TrainingTypeRepository extends MongoRepository<TrainingTypeDocument, String> {

    public List<TrainingTypeDocument> findAll();
    public TrainingTypeDocument findTrainingTypeById(String id);
    public TrainingTypeDocument findTrainingTypeByTrainingName(String trainingName);

    public boolean existsTrainingTypeById(String id);
    public boolean existsByTrainingName(String trainingName);

    public void deleteByTrainingName(String trainingName);
    public void removeTrainingTypeByTrainingName(String trainingName);

}
