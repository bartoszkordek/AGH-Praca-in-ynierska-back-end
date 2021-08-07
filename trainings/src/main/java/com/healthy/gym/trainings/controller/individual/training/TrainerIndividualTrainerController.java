package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.service.individual.training.TrainerIndividualTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/individual")
public class TrainerIndividualTrainerController {

    private final TrainerIndividualTrainingService trainerIndividualTrainingService;

    @Autowired
    public TrainerIndividualTrainerController(TrainerIndividualTrainingService trainerIndividualTrainingService) {
        this.trainerIndividualTrainingService = trainerIndividualTrainingService;
    }

    @PutMapping("/{trainingId}/accept")
    public IndividualTrainings acceptIndividualTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody final IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel
    ) throws RestException {
        try {
            return trainerIndividualTrainingService.acceptIndividualTraining(trainingId, individualTrainingsAcceptModel);
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
            return trainerIndividualTrainingService.rejectIndividualTraining(trainingId);
        } catch (NotExistingIndividualTrainingException
                | AlreadyDeclinedIndividualTrainingException
                | EmailSendingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
