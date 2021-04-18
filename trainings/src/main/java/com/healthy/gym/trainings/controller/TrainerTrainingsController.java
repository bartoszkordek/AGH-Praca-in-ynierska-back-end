package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.IndividualTrainingsAcceptModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TrainerTrainingsController {

    TrainingsService trainingsService;
    IndividualTrainingsService individualTrainingsService;

    public TrainerTrainingsController(TrainingsService trainingsService, IndividualTrainingsService individualTrainingsService){
        this.trainingsService = trainingsService;
        this.individualTrainingsService = individualTrainingsService;
    }

    @GetMapping("/group/{trainingId}/participants")
    public List<String> getTrainingParticipants(@PathVariable("trainingId") final String trainingId) throws RestException {
        try{
            return trainingsService.getTrainingParticipants(trainingId);
        } catch (NotExistingGroupTrainingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/individual/{trainingId}")
    public IndividualTrainings getIndividualTrainingById(@PathVariable("trainingId") final String trainingId) throws RestException {
        try{
            return individualTrainingsService.getIndividualTrainingById(trainingId);
        } catch (NotExistingIndividualTrainingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/individual/{trainingId}/accept")
    public IndividualTrainings acceptIndividualTraining(@PathVariable("trainingId") final String trainingId,
                                                        @Valid @RequestBody final IndividualTrainingsAcceptModel individualTrainingsAcceptModel) throws RestException {
        try{
            return individualTrainingsService.acceptIndividualTraining(trainingId, individualTrainingsAcceptModel);
        } catch (NotExistingIndividualTrainingException | AlreadyAcceptedIndividualTrainingException | HallNoOutOfRangeException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/individual/{trainingId}/decline")
    public IndividualTrainings acceptIndividualTraining(@PathVariable("trainingId") final String trainingId) throws RestException {
        try {
            return individualTrainingsService.declineIndividualTraining(trainingId);
        } catch (NotExistingIndividualTrainingException | AlreadyDeclinedIndividualTrainingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
