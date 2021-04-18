package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.IndividualTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingsRepository extends MongoRepository<IndividualTrainings, String> {

    public List<IndividualTrainings> findAll();

    public List<IndividualTrainings> findAllByAccepted(boolean accepted);

    public IndividualTrainings findIndividualTrainingsBy(String trainingId);

    public boolean existsIndividualTrainingsBy(String trainingId);
    public boolean existsIndividualTrainingsByAcceptedEquals(String trainingId, boolean accepted);
}
