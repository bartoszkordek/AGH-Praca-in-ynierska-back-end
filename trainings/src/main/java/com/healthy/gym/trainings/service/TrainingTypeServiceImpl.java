package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
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

    public List<TrainingTypeManagerViewModel> getAllTrainingTypesManagerView() {
        List<TrainingTypeDocument> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypeManagerViewModel> trainingTypeManagerViewModels = new ArrayList<>();
        for (TrainingTypeDocument trainingType : trainingTypes) {
            TrainingTypeManagerViewModel trainingTypeManagerViewModel = new TrainingTypeManagerViewModel(
                    trainingType.getId(),
                    trainingType.getName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
            );
            trainingTypeManagerViewModels.add(trainingTypeManagerViewModel);
        }

        return trainingTypeManagerViewModels;
    }

    public List<TrainingTypePublicViewModel> getAllTrainingTypesPublicView() {
        List<TrainingTypeDocument> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypePublicViewModel> trainingTypePublicViewModels = new ArrayList<>();
        for (TrainingTypeDocument trainingType : trainingTypes) {
            TrainingTypePublicViewModel trainingTypePublicViewModel = new TrainingTypePublicViewModel(
                    trainingType.getName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
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

        TrainingTypeDocument response = trainingTypeRepository.insert(new TrainingTypeDocument(trainingName, description, avatar));
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
        trainingType.setAvatar(avatar);

        return trainingTypeRepository.save(trainingType);
    }
}
