package com.healthy.gym.trainings.data.repository.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IndividualTrainingRepository extends MongoRepository<IndividualTrainingDocument, String> {

    List<IndividualTrainingDocument> findAllByAccepted(boolean accepted);

    IndividualTrainingDocument findIndividualTrainingsById(String trainingId);

    void deleteIndividualTrainingsById(String trainingId);
}
