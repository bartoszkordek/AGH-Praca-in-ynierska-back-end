package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.model.request.GroupTrainingRequestOld;

public interface GroupTrainingsDbRepository {

    boolean isAbilityToGroupTrainingEnrollment(String trainingId);

    boolean isAbilityToCreateTraining(GroupTrainingRequestOld groupTrainingModel);

    boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequestOld groupTrainingModel);
}
