package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeManagerViewModel> getAllTrainingTypesManagerView();

    List<TrainingTypePublicViewModel> getAllTrainingTypesPublicView();

    TrainingType getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType;

    TrainingType createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws DuplicatedTrainingTypes;

    TrainingType removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType;

    TrainingType updateTrainingTypeById(String trainingId, TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws NotExistingTrainingType, DuplicatedTrainingTypes;
}
