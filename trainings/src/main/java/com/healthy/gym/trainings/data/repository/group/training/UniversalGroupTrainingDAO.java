package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;

import java.time.LocalDateTime;
import java.util.List;

public interface UniversalGroupTrainingDAO {

    List<GroupTrainingDocument> getGroupTrainingDocuments(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<GroupTrainingDocument> getGroupTrainingDocumentsByTrainingType(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            TrainingTypeDocument trainingType
    );
}
