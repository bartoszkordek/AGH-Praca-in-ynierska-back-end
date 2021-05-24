package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
public class ManagerTrainingsController {

    GroupTrainingsService groupTrainingsService;
    IndividualTrainingsService individualTrainingsService;

    public ManagerTrainingsController(GroupTrainingsService groupTrainingsService, IndividualTrainingsService individualTrainingsService){
        this.groupTrainingsService = groupTrainingsService;
        this.individualTrainingsService = individualTrainingsService;
    }

    @PostMapping("/group")
    public GroupTrainings createGroupTraining(@Valid @RequestBody GroupTrainingModel groupTrainingModel) throws RestException {
        try{
            return groupTrainingsService.createGroupTraining(groupTrainingModel);
        } catch (TrainingCreationException | InvalidHourException |ParseException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/{trainingId}/remove")
    public GroupTrainings removeGroupTraining(@PathVariable("trainingId") final String trainingId) throws RestException {
        try{
            return groupTrainingsService.removeGroupTraining(trainingId);
        } catch (TrainingRemovalException | EmailSendingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/group/{trainingId}/update")
    public GroupTrainings updateGroupTraining(@PathVariable("trainingId") final String trainingId,
                                              @Valid @RequestBody GroupTrainingModel groupTrainingModelRequest) throws RestException {
        try{
            return groupTrainingsService.updateGroupTraining(trainingId, groupTrainingModelRequest);
        } catch (TrainingUpdateException | InvalidHourException | EmailSendingException e){
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
