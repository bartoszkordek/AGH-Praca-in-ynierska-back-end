package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainingTypeService {

    TrainingTypeDocument createTrainingType(TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile)
            throws DuplicatedTrainingTypeException;

    List<TrainingTypeDocument> getAllTrainingTypes() throws TrainingTypeNotFoundException;

    TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws TrainingTypeNotFoundException;

    TrainingTypeDocument updateTrainingTypeById(String trainingId, TrainingTypeModel trainingTypeModel, byte[] avatar)
            throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException;

    TrainingTypeDocument updateTrainingTypeById(
            String trainingId, TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile
    ) throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException;

    TrainingTypeDocument removeTrainingTypeById(String trainingName) throws TrainingTypeNotFoundException;
}
