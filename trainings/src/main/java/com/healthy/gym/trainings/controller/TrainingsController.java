package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.model.GroupTrainingsPublicViewModel;
import com.healthy.gym.trainings.model.TrainingTypeManagerViewModel;
import com.healthy.gym.trainings.model.TrainingTypePublicViewModel;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrainingsController {

    GroupTrainingsService groupTrainingsService;
    TrainingTypeService trainingTypeService;

    public TrainingsController(GroupTrainingsService groupTrainingsService,
                               TrainingTypeService trainingTypeService){
        this.groupTrainingsService = groupTrainingsService;
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping("/status")
    public String status(){
        return "OK";
    }

    @GetMapping("/test/document/first")
    public String getFirstTestDocument(){
        return groupTrainingsService.getFirstTestDocument();
    }

    @GetMapping("/group")
    public List<GroupTrainings> getGroupTrainings() {
        return groupTrainingsService.getGroupTrainings();
    }

    @GetMapping("/public/group")
    public List<GroupTrainingsPublicViewModel> getPublicGroupTrainings() throws InvalidHourException, InvalidDateException {
        return groupTrainingsService.getPublicGroupTrainings();
    }

    @GetMapping("/group/{trainingId}")
    public GroupTrainings getGroupTrainingById(@PathVariable("trainingId") final String trainingId) throws NotExistingGroupTrainingException {
        return groupTrainingsService.getGroupTrainingById(trainingId);
    }

    @GetMapping("/types")
    public List<? extends TrainingTypePublicViewModel> getAllTrainingTypes(@RequestParam("publicView") boolean publicView){
        if(!publicView){
            return trainingTypeService.getAllTrainingTypesManagerView();
        } else {
            return trainingTypeService.getAllTrainingTypesPublicView();
        }
    }
}
