package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingsRepository extends MongoRepository<IndividualTrainings, String> {

    List<IndividualTrainings> findIndividualTrainingsByClientIdEquals(String clientId);

    List<IndividualTrainings> findAllByAccepted(boolean accepted);

    IndividualTrainings findIndividualTrainingsById(String trainingId);

    boolean existsIndividualTrainingsById(String trainingId);

    boolean existsIndividualTrainingsByIdAndAcceptedEquals(String trainingId, boolean accepted);

    boolean existsIndividualTrainingsByIdAndDeclinedEquals(String trainingId, boolean declined);

    boolean existsIndividualTrainingsByIdAndClientIdEquals(String trainingId, String clientId);

    void deleteIndividualTrainingsById(String trainingId);
}
