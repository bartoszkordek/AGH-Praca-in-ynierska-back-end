package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingRepository extends MongoRepository<IndividualTrainingDocument, String> {

    List<IndividualTrainingDocument> findIndividualTrainingsByClientIdEquals(String clientId);

    List<IndividualTrainingDocument> findAllByAccepted(boolean accepted);

    IndividualTrainingDocument findIndividualTrainingsById(String trainingId);

    boolean existsIndividualTrainingsByIdAndClientIdEquals(String trainingId, String clientId);

    void deleteIndividualTrainingsById(String trainingId);
}
