package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.TrainingType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TrainingTypeRepository extends MongoRepository<TrainingType, String> {

    public List<TrainingType> findAll();

    public boolean existsByTrainingName(String trainingName);

}
