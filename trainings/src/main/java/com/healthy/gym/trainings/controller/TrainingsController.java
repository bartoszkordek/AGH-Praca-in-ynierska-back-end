package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrainingsController {

    GroupTrainingsService groupTrainingsService;

    public TrainingsController(GroupTrainingsService groupTrainingsService){
        this.groupTrainingsService = groupTrainingsService;
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

    @GetMapping("/group/{trainingId}")
    public GroupTrainings getGroupTrainingById(@PathVariable("trainingId") final String trainingId) throws NotExistingGroupTrainingException {
        return groupTrainingsService.getGroupTrainingById(trainingId);
    }
}
