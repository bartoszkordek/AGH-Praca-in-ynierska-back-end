package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.AlreadyDeclinedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.ResponseBindException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingAcceptanceRequest;
import com.healthy.gym.trainings.model.response.IndividualTrainingResponse;
import com.healthy.gym.trainings.service.individual.training.TrainerIndividualTrainingService;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/individual/trainer")
@Validated
public class TrainerIndividualTrainerController {

    private final TrainerIndividualTrainingService trainerIndividualTrainingService;
    private final Translator translator;

    @Autowired
    public TrainerIndividualTrainerController(
            TrainerIndividualTrainingService trainerIndividualTrainingService,
            Translator translator
    ) {
        this.trainerIndividualTrainingService = trainerIndividualTrainingService;
        this.translator = translator;
    }

    @PutMapping("/{trainingId}/accept")
    public ResponseEntity<IndividualTrainingResponse> acceptIndividualTraining(
            @PathVariable @ValidIDFormat final String trainingId,
            @Valid @RequestBody final IndividualTrainingAcceptanceRequest individualTrainingsAcceptModel,
            final BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            IndividualTrainingDTO removedEnrolmentTraining = trainerIndividualTrainingService
                    .acceptIndividualTraining(trainingId, individualTrainingsAcceptModel);
            String message = translator.toLocale("enrollment.remove");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, removedEnrolmentTraining));

        } catch (AlreadyAcceptedIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.already.accepted.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.location.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PutMapping("/{trainingId}/decline")
    public ResponseEntity<IndividualTrainingResponse> rejectIndividualTraining(
            @PathVariable("trainingId") final String trainingId
    ) {
        try {
            IndividualTrainingDTO removedEnrolmentTraining = trainerIndividualTrainingService
                    .rejectIndividualTraining(trainingId);
            String message = translator.toLocale("enrollment.remove");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, removedEnrolmentTraining));

        } catch (AlreadyDeclinedIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.already.declined.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
