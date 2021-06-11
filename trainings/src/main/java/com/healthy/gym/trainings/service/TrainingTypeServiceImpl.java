package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypeException;
import com.healthy.gym.trainings.exception.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.request.TrainingTypeRequest;
import com.healthy.gym.trainings.model.response.TrainingTypeManagerResponse;
import com.healthy.gym.trainings.model.response.TrainingTypePublicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDAO trainingTypeRepository;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDAO trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public List<TrainingTypeManagerResponse> getAllTrainingTypesManagerView() {
        List<TrainingTypeDocument> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypeManagerResponse> trainingTypeManagerViewModels = new ArrayList<>();
        for (TrainingTypeDocument trainingType : trainingTypes) {
            TrainingTypeManagerResponse trainingTypeManagerViewModel = new TrainingTypeManagerResponse(
                    trainingType.getId(),
                    trainingType.getName(),
                    trainingType.getDescription(),
                    null //trainingType.getImageDocument()
            );
            trainingTypeManagerViewModels.add(trainingTypeManagerViewModel);
        }

        return trainingTypeManagerViewModels;
    }

    public List<TrainingTypePublicResponse> getAllTrainingTypesPublicView() {
        List<TrainingTypeDocument> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypePublicResponse> trainingTypePublicViewModels = new ArrayList<>();
        for (TrainingTypeDocument trainingType : trainingTypes) {
            TrainingTypePublicResponse trainingTypePublicViewModel = new TrainingTypePublicResponse(
                    trainingType.getName(),
                    trainingType.getDescription(),
                    null //trainingType.getAvatar()
            );
            trainingTypePublicViewModels.add(trainingTypePublicViewModel);
        }

        return trainingTypePublicViewModels;
    }

    @Override
    public List<TrainingTypeDocument> getAllTrainingTypes() {
        return null;
    }

    public TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws TrainingTypeNotFoundException {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new TrainingTypeNotFoundException("Training type of id: " + trainingTypeId + " not exist.");
        }
        return trainingTypeRepository.findByTrainingTypeId(trainingTypeId);
    }

    public TrainingTypeDocument createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar) throws DuplicatedTrainingTypeException {
        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        if (trainingTypeRepository.existsByName(trainingName)) {
            throw new DuplicatedTrainingTypeException("Training type of name: " + trainingName + " already exists.");
        }

        TrainingTypeDocument response = trainingTypeRepository
                .insert(new TrainingTypeDocument(null, trainingName, description, null, null));
        return response;
    }

    @Override
    public TrainingTypeDocument createTrainingType(TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile) throws DuplicatedTrainingTypeException {
        return null;
    }

    @Override
    public TrainingTypeDocument createTrainingType(TrainingTypeRequest trainingTypeRequest) throws DuplicatedTrainingTypeException {
        return null;
    }

    public TrainingTypeDocument removeTrainingTypeByName(String trainingName) throws TrainingTypeNotFoundException {
        if (!trainingTypeRepository.existsByName(trainingName)) {
            throw new TrainingTypeNotFoundException("Training type of name: " + trainingName + " not exist.");
        }

        TrainingTypeDocument trainingTypeToRemove = trainingTypeRepository.findByName(trainingName);
        trainingTypeRepository.removeByName(trainingName);

        return trainingTypeToRemove;
    }

    public TrainingTypeDocument updateTrainingTypeById(String trainingTypeId, TrainingTypeModel trainingTypeModel, byte[] avatar) throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new TrainingTypeNotFoundException("Training type of id: " + trainingTypeId + " not exist.");
        }

        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        TrainingTypeDocument trainingType = trainingTypeRepository.findByTrainingTypeId(trainingTypeId);
        trainingType.setName(trainingName);
        trainingType.setDescription(description);
//        trainingType.setAvatar(avatar);

        return trainingTypeRepository.save(trainingType);
    }

    @Override
    public TrainingTypeDocument updateTrainingTypeById(String trainingId, TrainingTypeRequest trainingTypeRequest, MultipartFile multipartFile) throws TrainingTypeNotFoundException, DuplicatedTrainingTypeException {
        return null;
    }
}
