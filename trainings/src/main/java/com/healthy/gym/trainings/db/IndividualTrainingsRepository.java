package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.IndividualTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingsRepository extends MongoRepository<IndividualTrainings, String> {

    public List<IndividualTrainings> findAll();
}
