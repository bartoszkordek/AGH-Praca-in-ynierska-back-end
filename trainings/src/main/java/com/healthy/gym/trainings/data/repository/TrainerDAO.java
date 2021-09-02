package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.UserDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface TrainerDAO {
    List<GroupTrainingDocument> getTrainerGroupTrainings(UserDocument user,
                                                         LocalDateTime startDateTime,
                                                         LocalDateTime endDateTime);

    List<IndividualTrainingDocument> getTrainerIndividualTrainings(UserDocument user,
                                                             LocalDateTime startDateTime,
                                                             LocalDateTime endDateTime);
}
