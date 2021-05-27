package com.healthy.gym.trainings.mock;

import com.healthy.gym.trainings.db.TrainingTypeRepository;
import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.exception.NotExistingTrainingType;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TrainingTypeServiceImpl extends TrainingTypeService {

    @Autowired
    TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        super(trainingTypeRepository);
    }

    @Override
    public List<TrainingTypeManagerViewModel> getAllTrainingTypesManagerView(){
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypeManagerViewModel> trainingTypeManagerViewModels = new ArrayList<>();
        for(TrainingType trainingType : trainingTypes) {
            TrainingTypeManagerViewModel trainingTypeManagerViewModel = new TrainingTypeManagerViewModel(
                    trainingType.getId(),
                    trainingType.getTrainingName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
            );
            trainingTypeManagerViewModels.add(trainingTypeManagerViewModel);
        }

        return trainingTypeManagerViewModels;
    }

    @Override
    public List<TrainingTypePublicViewModel> getAllTrainingTypesPublicView(){
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        List<TrainingTypePublicViewModel> trainingTypePublicViewModels = new ArrayList<>();
        for(TrainingType trainingType : trainingTypes){
            TrainingTypePublicViewModel trainingTypePublicViewModel = new TrainingTypePublicViewModel(
                    trainingType.getTrainingName(),
                    trainingType.getDescription(),
                    trainingType.getAvatar()
            );
            trainingTypePublicViewModels.add(trainingTypePublicViewModel);
        }

        return trainingTypePublicViewModels;
    }

    @Override
    public TrainingType getTrainingTypeById(String trainingTypeId) throws NotExistingTrainingType {
        if(!trainingTypeRepository.existsTrainingTypeById(trainingTypeId)){
            throw new NotExistingTrainingType("Training type of id: " + trainingTypeId + " not exist.");
        }
        return trainingTypeRepository.findTrainingTypeById(trainingTypeId);
    }

    @Override
    public TrainingType createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar) throws DuplicatedTrainingTypes {
        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        if(trainingTypeRepository.existsByTrainingName(trainingName)){
            throw new DuplicatedTrainingTypes("Training type of name: " + trainingName + " already exists.");
        }

        TrainingType response = trainingTypeRepository.insert(new TrainingType(trainingName, description, avatar));
        return response;
    }

    @Override
    public TrainingType removeTrainingTypeByName(String trainingName) throws NotExistingTrainingType {
        if(!trainingTypeRepository.existsByTrainingName(trainingName)){
            throw new NotExistingTrainingType("Training type of name: " + trainingName + " not exist.");
        }

        TrainingType trainingTypeToRemove = trainingTypeRepository.findTrainingTypeByTrainingName(trainingName);
        trainingTypeRepository.removeTrainingTypeByTrainingName(trainingName);

        return trainingTypeToRemove;
    }

    @Override
    public TrainingType updateTrainingTypeById(String trainingId, TrainingTypeModel trainingTypeModel, byte[] avatar) throws NotExistingTrainingType, DuplicatedTrainingTypes {
        if(!trainingTypeRepository.existsTrainingTypeById(trainingId)){
            throw new NotExistingTrainingType("Training type of id: " + trainingId + " not exist.");
        }

        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        TrainingType trainingType = trainingTypeRepository.findTrainingTypeById(trainingId);
        trainingType.setTrainingName(trainingName);
        trainingType.setDescription(description);
        trainingType.setAvatar(avatar);

        return trainingTypeRepository.save(trainingType);
    }
}
