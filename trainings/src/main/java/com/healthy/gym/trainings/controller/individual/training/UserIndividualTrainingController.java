package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.data.document.IndividualTrainings;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.RetroIndividualTrainingException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/individual")
public class UserIndividualTrainingController {

    private final UserIndividualTrainingService userIndividualTrainingService;

    @Autowired
    public UserIndividualTrainingController(UserIndividualTrainingService userIndividualTrainingService) {
        this.userIndividualTrainingService = userIndividualTrainingService;
    }

    @GetMapping("/user/{userId}")
    public List<IndividualTrainings> getMyAllIndividualTrainings(
            @PathVariable final String userId
    ) throws UserNotFoundException {
        return userIndividualTrainingService.getMyAllTrainings(userId);
    }

    @PostMapping("/request")
    public IndividualTrainings createIndividualTrainingRequest(
            @Valid @RequestBody final IndividualTrainingRequest individualTrainingsRequestModel,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return userIndividualTrainingService
                    .createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
        } catch (InvalidHourException | RetroIndividualTrainingException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/{trainingId}")
    public IndividualTrainings cancelIndividualTrainingRequest(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return userIndividualTrainingService.cancelIndividualTrainingRequest(trainingId, clientId);
        } catch (NotExistingIndividualTrainingException | ParseException | RetroIndividualTrainingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e) {
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }
}
