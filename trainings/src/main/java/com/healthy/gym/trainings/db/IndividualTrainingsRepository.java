package com.healthy.gym.trainings.db;

import com.healthy.gym.trainings.entity.IndividualTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingsRepository extends MongoRepository<IndividualTrainings, String> {

    public List<IndividualTrainings> findAll();
    public List<IndividualTrainings> findIndividualTrainingsByClientIdEquals(String clientId);

    public List<IndividualTrainings> findAllByAccepted(boolean accepted);

    public IndividualTrainings findIndividualTrainingsById(String trainingId);

    public boolean existsIndividualTrainingsById(String trainingId);
    public boolean existsIndividualTrainingsByIdAndAcceptedEquals(String trainingId, boolean accepted);
    public boolean existsIndividualTrainingsByIdAndDeclinedEquals(String trainingId, boolean declined);
    public boolean existsIndividualTrainingsByIdAndClientIdEquals(String trainingId, String clientId);

    public void deleteIndividualTrainingsById(String trainingId);
}
