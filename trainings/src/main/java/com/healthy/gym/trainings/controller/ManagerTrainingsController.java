package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainings;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.entity.TrainingType;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingModel;
import com.healthy.gym.trainings.model.TrainingTypeModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import com.healthy.gym.trainings.service.TrainingTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
public class ManagerTrainingsController {

    GroupTrainingsService groupTrainingsService;
    IndividualTrainingsService individualTrainingsService;
    TrainingTypeService trainingTypeService;

    public ManagerTrainingsController(GroupTrainingsService groupTrainingsService,
                                      IndividualTrainingsService individualTrainingsService,
                                      TrainingTypeService trainingTypeService){
        this.groupTrainingsService = groupTrainingsService;
        this.individualTrainingsService = individualTrainingsService;
        this.trainingTypeService = trainingTypeService;
    }

    private String genericErrorMessageWhileIssuesWithPhotoProcessing = "Errors while proto processing.";

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
        } catch (TrainingUpdateException | InvalidHourException | EmailSendingException | ParseException e){
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

    @PostMapping(
            value = "/type",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrainingType createTrainingType(@RequestParam("trainingName") String trainingName,
                                           @RequestParam("description") String description,
                                           @RequestParam("avatar") MultipartFile multipartFile) throws RestException {
        try{
            TrainingTypeModel trainingTypeModel = new TrainingTypeModel(trainingName, description);
            return trainingTypeService.createTrainingType(trainingTypeModel, multipartFile.getBytes());
        } catch (DuplicatedTrainingTypes e){
            throw new RestException(e.getMessage(), HttpStatus.CONFLICT, e);
        } catch (IOException e){
            throw new RestException(genericErrorMessageWhileIssuesWithPhotoProcessing, HttpStatus.BAD_REQUEST, e);
        }
    }


    @DeleteMapping("/type/{trainingName}")
    public TrainingType removeTrainingTypeByName(@PathVariable("trainingName") final String trainingName) throws RestException {
        try{
            return trainingTypeService.removeTrainingTypeByName(trainingName);
        } catch (NotExistingTrainingType e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }


    @PutMapping(
            value = "/type/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TrainingType updateTrainingTypeById(@PathVariable("id") final String id,
                                               @RequestParam("trainingName") String trainingName,
                                               @RequestParam("description") String description,
                                               @RequestParam("avatar") MultipartFile multipartFile) throws RestException {
        try{
            TrainingTypeModel trainingTypeModel = new TrainingTypeModel(trainingName, description);
            return trainingTypeService. updateTrainingTypeById(id, trainingTypeModel, multipartFile.getBytes());
        } catch (NotExistingTrainingType | IOException | DuplicatedTrainingTypes e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

}
