package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.model.response.TrainingTypeManagerResponse;
import com.healthy.gym.trainings.model.response.TrainingTypePublicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeManagerResponse> getAllTrainingTypesManagerView();

    List<TrainingTypePublicResponse> getAllTrainingTypesPublicView();

    TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType;

    TrainingTypeDocument createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws DuplicatedTrainingTypeException;

    TrainingTypeDocument createTrainingType(TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile)
            throws DuplicatedTrainingTypeException;

    TrainingTypeDocument createTrainingType(TrainingTypeRequest trainingTypeRequest)
            throws DuplicatedTrainingTypeException;

    TrainingTypeDocument removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType;

    TrainingTypeDocument updateTrainingTypeById(String trainingId, TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws NotExistingTrainingType, DuplicatedTrainingTypeException;
}
