package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidTrainerSpecifiedException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.IndividualTrainingRequest;
import com.healthy.gym.trainings.model.response.IndividualTrainingResponse;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import com.healthy.gym.trainings.validation.ValidDateFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/individual/user/{userId}")
public class UserIndividualTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";
    private static final String EXCEPTION_NOT_FOUND_USER_ID = "exception.not.found.user.id";

    private final UserIndividualTrainingService userIndividualTrainingService;
    private final Translator translator;

    @Autowired
    public UserIndividualTrainingController(
            UserIndividualTrainingService userIndividualTrainingService,
            Translator translator
    ) {
        this.userIndividualTrainingService = userIndividualTrainingService;
        this.translator = translator;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @GetMapping
    public List<IndividualTrainingDTO> getMyAllIndividualTrainings(
            @PathVariable @ValidIDFormat final String userId,
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return userIndividualTrainingService.getMyAllTrainings(userId, startDate, endDate);

        } catch (NoIndividualTrainingFoundException exception) {
            String reason = translator.toLocale("exception.no.individual.training.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @PostMapping
    public ResponseEntity<IndividualTrainingResponse> createIndividualTrainingRequest(
            @PathVariable final String userId,
            @Valid @RequestBody final IndividualTrainingRequest individualTrainingsRequestModel,
            final BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            IndividualTrainingDTO individualTrainingDTO = userIndividualTrainingService
                    .createIndividualTrainingRequest(individualTrainingsRequestModel, userId);
            String message = translator.toLocale("enrollment.success.individual");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, individualTrainingDTO));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (InvalidTrainerSpecifiedException exception) {
            String reason = translator.toLocale("exception.invalid.trainer.specified");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerNotFoundException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.individual.training.not.available");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @DeleteMapping("/training/{trainingId}")
    @Validated
    public ResponseEntity<IndividualTrainingResponse> cancelIndividualTrainingRequest(
            @PathVariable @ValidIDFormat final String userId,
            @PathVariable @ValidIDFormat final String trainingId
    ) {
        try {
            IndividualTrainingDTO removedEnrolmentTraining = userIndividualTrainingService
                    .cancelIndividualTrainingRequest(trainingId, userId);
            String message = translator.toLocale("enrollment.remove");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new IndividualTrainingResponse(message, removedEnrolmentTraining));

        } catch (AlreadyCancelledIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.already.cancelled.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (IndividualTrainingHasBeenRejectedException exception) {
            String reason = translator.toLocale("exception.already.rejected.individual.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date.enrollment.remove");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (UserIsNotParticipantException exception) {
            String reason = translator.toLocale("exception.user.is.not.participant");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
