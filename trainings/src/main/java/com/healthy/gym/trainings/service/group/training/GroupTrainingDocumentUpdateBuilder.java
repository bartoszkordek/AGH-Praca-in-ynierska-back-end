package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;

public interface GroupTrainingDocumentUpdateBuilder {

    GroupTrainingDocumentUpdateBuilder setGroupTrainingDocumentToUpdate(GroupTrainingDocument groupTraining);

    GroupTrainingDocumentUpdateBuilder setGroupTrainingRequest(ManagerGroupTrainingRequest groupTrainingRequest);

    GroupTrainingDocumentUpdateBuilder updateTrainingType() throws TrainingTypeNotFoundException;

    GroupTrainingDocumentUpdateBuilder updateTrainers() throws TrainerNotFoundException;

    GroupTrainingDocumentUpdateBuilder updateStartDate() throws PastDateException;

    GroupTrainingDocumentUpdateBuilder updateEndDate() throws PastDateException;

    GroupTrainingDocumentUpdateBuilder updateLocation() throws LocationNotFoundException;

    GroupTrainingDocumentUpdateBuilder updateLimit();

    GroupTrainingDocument update();
}
