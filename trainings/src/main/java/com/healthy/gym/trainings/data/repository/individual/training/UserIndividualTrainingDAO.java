package com.healthy.gym.trainings.data.repository.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface UserIndividualTrainingDAO {
    List<IndividualTrainingDocument> findAllIndividualTrainingsWithDatesByUserDocument(UserDocument userDocument,
                                                                                       LocalDateTime startDateTime,
                                                                                       LocalDateTime endDateTime);
}
