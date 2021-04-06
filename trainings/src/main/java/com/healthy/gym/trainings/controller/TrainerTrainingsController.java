package com.healthy.gym.trainings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/trainings")
public class TrainerTrainingsController {

    TrainingsService trainingsService;

    public TrainerTrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @GetMapping("/group/participants/{trainingId}")
    public List<String> getTrainingParticipants(@PathVariable("trainingId") final String trainingId) throws RestException {
        try{
            return trainingsService.getTrainingParticipants(trainingId);
        } catch (NotExistingGroupTrainingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
