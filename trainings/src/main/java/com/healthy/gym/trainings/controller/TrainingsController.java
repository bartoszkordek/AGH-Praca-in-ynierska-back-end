package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.service.GroupTrainingsService;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainingsController {

    GroupTrainingsService groupTrainingsService;
    TrainingTypeService trainingTypeService;

    public TrainingsController(GroupTrainingsService groupTrainingsService,
                               TrainingTypeService trainingTypeService) {
        this.groupTrainingsService = groupTrainingsService;
        this.trainingTypeService = trainingTypeService;
    }

    @GetMapping("/status")
    public String status() {
        return "OK";
    }

    @GetMapping("/test/document/first")
    public String getFirstTestDocument() {
        return groupTrainingsService.getFirstTestDocument();
    }


}
