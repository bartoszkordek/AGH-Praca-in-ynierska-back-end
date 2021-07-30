package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.TrainerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrainersRepository extends MongoRepository<TrainerDocument, String> {
    TrainerDocument findByTrainerId(String trainerId);
}
