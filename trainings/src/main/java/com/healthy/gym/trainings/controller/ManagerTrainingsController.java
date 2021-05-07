package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
public class ManagerTrainingsController {

    TrainingsService trainingsService;
    IndividualTrainingsService individualTrainingsService;

    public ManagerTrainingsController(TrainingsService trainingsService, IndividualTrainingsService individualTrainingsService){
        this.trainingsService = trainingsService;
        this.individualTrainingsService = individualTrainingsService;
    }

    @PostMapping("/group/create")
    public GroupTrainings createGroupTraining(@Valid @RequestBody GroupTrainingModel groupTrainingModel) throws RestException {
        try{
            return trainingsService.createGroupTraining(groupTrainingModel);
        } catch (TrainingCreationException | ParseException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/{trainingId}/remove")
    public GroupTrainings removeGroupTraining(@PathVariable("trainingId") final String trainingId) throws RestException {
        try{
            return trainingsService.removeGroupTraining(trainingId);
        } catch (TrainingRemovalException | EmailSendingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/group/{trainingId}/update")
    public GroupTrainings updateGroupTraining(@PathVariable("trainingId") final String trainingId,
                                              @Valid @RequestBody GroupTrainingModel groupTrainingModelRequest) throws RestException {
        try{
            return trainingsService.updateGroupTraining(trainingId, groupTrainingModelRequest);
        } catch (TrainingUpdateException | EmailSendingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/individual")
    public List<IndividualTrainings> getAllIndividualTrainingRequests(){
        return individualTrainingsService.getAllIndividualTrainings();
    }

    @GetMapping("/individual/all/accepted")
    public List<IndividualTrainings> getAllAcceptedIndividualTrainingRequests(){
        return individualTrainingsService.getAllAcceptedIndividualTrainings();
    }
}
