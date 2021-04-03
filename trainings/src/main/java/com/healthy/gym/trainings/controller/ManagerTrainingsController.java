package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/trainings")
public class ManagerTrainingsController {

    TrainingsService trainingsService;

    public ManagerTrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @RequestMapping("/group/create")
    public GroupTrainings createGroupTraining(@Valid @RequestBody GroupTrainingModel groupTrainingModel) throws RestException {
        //try{
            return trainingsService.createGroupTraining(groupTrainingModel);
        //} catch (Exception e){

        //}

    }
}
