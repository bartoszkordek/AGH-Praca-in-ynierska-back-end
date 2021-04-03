package com.healthy.gym.trainings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainingsController {

    TrainingsService trainingsService;

    public TrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @GetMapping("/status")
    public String status(){
        return "OK";
    }

    @GetMapping("/test/document/first")
    public String getFirstTestDocument(){
        return trainingsService.getFirstTestDocument();
    }

    @GetMapping("/group")
    public List<GroupTrainings> getGroupTrainings() throws JsonProcessingException {
        return trainingsService.getGroupTrainings();
    }
}
