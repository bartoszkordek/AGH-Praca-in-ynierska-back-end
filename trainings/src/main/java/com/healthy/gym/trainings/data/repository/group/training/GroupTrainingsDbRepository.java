package com.healthy.gym.trainings.data.repository.group.training;

import com.healthy.gym.trainings.model.request.GroupTrainingRequest;

public interface GroupTrainingsDbRepository {

    boolean isAbilityToGroupTrainingEnrollment(String trainingId);

    boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel);

    boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel);
}
