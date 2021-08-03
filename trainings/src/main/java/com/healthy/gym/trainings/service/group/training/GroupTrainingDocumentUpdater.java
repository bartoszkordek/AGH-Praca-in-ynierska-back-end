package com.healthy.gym.trainings.service.group.training;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;

public interface GroupTrainingDocumentUpdater {

    GroupTrainingDocumentUpdater setGroupTrainingDocumentToUpdate(GroupTrainingDocument groupTraining);

    GroupTrainingDocumentUpdater setGroupTrainingRequest(ManagerGroupTrainingRequest groupTrainingRequest);

    GroupTrainingDocumentUpdater updateTrainingType() throws TrainingTypeNotFoundException;

    GroupTrainingDocumentUpdater updateTrainers() throws TrainerNotFoundException;

    GroupTrainingDocumentUpdater updateStartDate() throws PastDateException;

    GroupTrainingDocumentUpdater updateEndDate() throws PastDateException;

    GroupTrainingDocumentUpdater updateLocation() throws LocationNotFoundException;

    GroupTrainingDocumentUpdater updateLimit();

    GroupTrainingDocument update();
}
