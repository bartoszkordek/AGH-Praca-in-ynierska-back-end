package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.TrainingEnrollmentException;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trainings")
public class SignedClientTrainingsController {

    TrainingsService trainingsService;

    public SignedClientTrainingsController(TrainingsService trainingsService){
        this.trainingsService = trainingsService;
    }

    @RequestMapping("/group/enroll/{trainingId}")
    public void enrollToGroupTraining(@PathVariable("trainingId") final String trainingId,
                                      @RequestParam(required = true) final String clientId) throws RestException {
        System.out.println(clientId);
        System.out.println(trainingId);
        try{
            trainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }
}
