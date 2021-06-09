package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.IndividualTrainingsAcceptModel;
import com.healthy.gym.trainings.model.request.IndividualTrainingsRequestModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/individual")
public class IndividualTrainingController {

    private final IndividualTrainingsService individualTrainingsService;

    @Autowired
    public IndividualTrainingController(IndividualTrainingsService individualTrainingsService) {
        this.individualTrainingsService = individualTrainingsService;
    }

    @PostMapping("/request")
    public IndividualTrainings createIndividualTrainingRequest(
            @Valid @RequestBody final IndividualTrainingsRequestModel individualTrainingsRequestModel,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return individualTrainingsService
                    .createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
        } catch (InvalidHourException | RetroIndividualTrainingException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/{trainingId}")
    public IndividualTrainings getIndividualTrainingById(
            @PathVariable("trainingId") final String trainingId
    ) throws RestException {
        try {
            return individualTrainingsService.getIndividualTrainingById(trainingId);
        } catch (NotExistingIndividualTrainingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO only for admin, paginacja, filtrowanie po datach
    @GetMapping
    public List<IndividualTrainings> getAllIndividualTrainingRequests() {
        return individualTrainingsService.getAllIndividualTrainings();
    }

    //TODO only for admin, paginacja, filtrowanie po datach
    @GetMapping("/all/accepted")
    public List<IndividualTrainings> getAllAcceptedIndividualTrainingRequests() {
        return individualTrainingsService.getAllAcceptedIndividualTrainings();
    }

    @GetMapping("/user/{userId}")
    public List<IndividualTrainings> getMyAllIndividualTrainings(
            @PathVariable final String userId
    ) {
        return individualTrainingsService.getMyAllTrainings(userId);
    }

    @PutMapping("/{trainingId}/accept")
    public IndividualTrainings acceptIndividualTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody final IndividualTrainingsAcceptModel individualTrainingsAcceptModel
    ) throws RestException {
        try {
            return individualTrainingsService.acceptIndividualTraining(trainingId, individualTrainingsAcceptModel);
        } catch (NotExistingIndividualTrainingException | AlreadyAcceptedIndividualTrainingException
                | RetroIndividualTrainingException | HallNoOutOfRangeException | ParseException
                | EmailSendingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PutMapping("/{trainingId}/decline")
    public IndividualTrainings acceptIndividualTraining(
            @PathVariable("trainingId") final String trainingId
    ) throws RestException {
        try {
            return individualTrainingsService.declineIndividualTraining(trainingId);
        } catch (NotExistingIndividualTrainingException
                | AlreadyDeclinedIndividualTrainingException
                | EmailSendingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/{trainingId}")
    public IndividualTrainings cancelIndividualTrainingRequest(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return individualTrainingsService.cancelIndividualTrainingRequest(trainingId, clientId);
        } catch (NotExistingIndividualTrainingException | ParseException | RetroIndividualTrainingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e) {
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }
}
