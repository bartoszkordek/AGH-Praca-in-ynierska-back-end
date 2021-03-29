package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/test/documents")
    public String getTestDocuments(){
        return trainingsService.getFirstTestDocument();
    }

}
