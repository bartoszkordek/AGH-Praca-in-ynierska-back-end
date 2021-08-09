package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.AlreadyAcceptedIndividualTrainingException;
import com.healthy.gym.trainings.exception.AlreadyRejectedIndividualTrainingException;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.model.response.IndividualTrainingResponse;
import com.healthy.gym.trainings.service.individual.training.TrainerIndividualTrainingService;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/individual/trainer/{userId}/training/{trainingId}")
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

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER') and principal==#userId)")
    public ResponseEntity<IndividualTrainingResponse> acceptIndividualTraining(
            @PathVariable @ValidIDFormat final String userId,
            @PathVariable @ValidIDFormat final String trainingId,
            @RequestParam @ValidIDFormat final String locationId
    ) {
        try {
            IndividualTrainingDTO removedEnrolmentTraining = trainerIndividualTrainingService
                    .acceptIndividualTraining(userId, trainingId, locationId);
            String message = translator.toLocale("enrollment.individual.accepted");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, removedEnrolmentTraining));

        } catch (AlreadyAcceptedIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.already.accepted.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (LocationOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.location.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRAINER') and principal==#userId)")
    public ResponseEntity<IndividualTrainingResponse> rejectIndividualTraining(
            @PathVariable @ValidIDFormat final String userId,
            @PathVariable @ValidIDFormat final String trainingId
    ) {
        try {
            IndividualTrainingDTO removedEnrolmentTraining = trainerIndividualTrainingService
                    .rejectIndividualTraining(userId, trainingId);
            String message = translator.toLocale("enrollment.individual.rejected");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, removedEnrolmentTraining));

        } catch (AlreadyRejectedIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.already.rejected.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date.individual.training.reject");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
