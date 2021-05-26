package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.db.TrainingTypeRepository;
import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.DuplicatedTrainingTypes;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingTypeService {

    @Autowired
    TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository){
        this.trainingTypeRepository = trainingTypeRepository;
    }

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
            System.out.println("trainingType.getTrainingName(): " + trainingType.getTrainingName());
            System.out.println("trainingType.getDescription(): " + trainingType.getDescription());
            System.out.println("trainingType.getAvatar(): " + trainingType.getAvatar());
            trainingTypeManagerViewModels.add(trainingTypeManagerViewModel);
        }

        return trainingTypeManagerViewModels;
    }

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

    public TrainingType createTrainingType(TrainingTypeModel trainingTypeModel, byte[] avatar) throws DuplicatedTrainingTypes {
        String trainingName = trainingTypeModel.getTrainingName();
        String description = trainingTypeModel.getDescription();
        if(trainingTypeRepository.existsByTrainingName(trainingName)){
            throw new DuplicatedTrainingTypes("Training type of name: " + trainingName + " already exists.");
        }

        TrainingType response = trainingTypeRepository.insert(new TrainingType(trainingName, description, avatar));
        return response;
    }
}
