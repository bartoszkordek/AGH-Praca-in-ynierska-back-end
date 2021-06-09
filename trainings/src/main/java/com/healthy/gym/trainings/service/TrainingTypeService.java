package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.response.TrainingTypeManagerResponse;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.response.TrainingTypePublicResponse;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeManagerResponse> getAllTrainingTypesManagerView();

    List<TrainingTypePublicResponse> getAllTrainingTypesPublicView();

    TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType;

    TrainingTypeDocument createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws DuplicatedTrainingTypes;

    TrainingTypeDocument removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType;

    TrainingTypeDocument updateTrainingTypeById(String trainingId, TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws NotExistingTrainingType, DuplicatedTrainingTypes;
}
