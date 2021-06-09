package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.response.TrainingTypeManagerResponse;
import com.healthy.gym.trainings.model.other.TrainingTypeModel;
import com.healthy.gym.trainings.model.response.TrainingTypePublicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TrainingTypeDocument getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new NotExistingTrainingType("Training type of id: " + trainingTypeId + " not exist.");
        }
        return trainingTypeRepository.findByTrainingTypeId(trainingTypeId);
    }

    public TrainingTypeDocument createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar) throws DuplicatedTrainingTypes {
        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        if (trainingTypeRepository.existsByName(trainingName)) {
            throw new DuplicatedTrainingTypes("Training type of name: " + trainingName + " already exists.");
        }

        TrainingTypeDocument response = trainingTypeRepository
                .insert(new TrainingTypeDocument(null, trainingName, description, null, null));
        return response;
    }

    public TrainingTypeDocument removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType {
        if (!trainingTypeRepository.existsByName(trainingName)) {
            throw new NotExistingTrainingType("Training type of name: " + trainingName + " not exist.");
        }

        TrainingTypeDocument trainingTypeToRemove = trainingTypeRepository.findByName(trainingName);
        trainingTypeRepository.removeByName(trainingName);

        return trainingTypeToRemove;
    }

    public TrainingTypeDocument updateTrainingTypeById(String trainingTypeId, TrainingTypeModel trainingTypeModel, byte[] avatar) throws NotExistingTrainingType, DuplicatedTrainingTypes {
        if (!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)) {
            throw new NotExistingTrainingType("Training type of id: " + trainingTypeId + " not exist.");
        }

        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        TrainingTypeDocument trainingType = trainingTypeRepository.findByTrainingTypeId(trainingTypeId);
        trainingType.setName(trainingName);
        trainingType.setDescription(description);
//        trainingType.setAvatar(avatar);

        return trainingTypeRepository.save(trainingType);
    }
}
