package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.TrainingType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TrainingTypeRepository extends MongoRepository<TrainingType, String> {

    public List<TrainingType> findAll();
    public TrainingType findTrainingTypeById(String id);
    public TrainingType findTrainingTypeByTrainingName(String trainingName);

    public boolean existsTrainingTypeById(String id);
    public boolean existsByTrainingName(String trainingName);

    public void deleteByTrainingName(String trainingName);
    public void removeTrainingTypeByTrainingName(String trainingName);

}
