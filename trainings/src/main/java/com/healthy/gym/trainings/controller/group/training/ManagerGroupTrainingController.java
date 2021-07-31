package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.exception.training.TrainingRemovalException;
import com.healthy.gym.trainings.exception.training.TrainingUpdateException;
import com.healthy.gym.trainings.model.request.CreateGroupTrainingRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.CreateGroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.ParseException;

@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/group", consumes = MediaType.APPLICATION_JSON_VALUE)
public class ManagerGroupTrainingController {
    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private final Translator translator;
    private final ManagerGroupTrainingService managerGroupTrainingService;

    @Autowired
    public ManagerGroupTrainingController(
            Translator translator,
            ManagerGroupTrainingService managerGroupTrainingService
    ) {
        this.translator = translator;
        this.managerGroupTrainingService = managerGroupTrainingService;
    }

    @PostMapping
    public ResponseEntity<CreateGroupTrainingResponse> createGroupTraining(
            @Valid @RequestBody CreateGroupTrainingRequest createGroupTrainingRequest,
            BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            GroupTrainingDTO createdTraining = managerGroupTrainingService
                    .createGroupTraining(createGroupTrainingRequest);
            String message = translator.toLocale("request.create.training.success");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new CreateGroupTrainingResponse(message, createdTraining));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerNotFoundException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainingType.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.location.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PutMapping("/{trainingId}")
    public GroupTrainingResponse updateGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody GroupTrainingRequest groupTrainingModelRequest
    ) {
        try {
            return managerGroupTrainingService.updateGroupTraining(trainingId, groupTrainingModelRequest);

        } catch (InvalidHourException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingUpdateException e) {
            String reason = translator.toLocale("exception.group.training.update");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (EmailSendingException e) {
            String reason = translator.toLocale("exception.email.sending");
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @DeleteMapping("/{trainingId}")
    public GroupTrainingResponse removeGroupTraining(
            @PathVariable("trainingId") final String trainingId
    ) {
        try {
            return managerGroupTrainingService.removeGroupTraining(trainingId);

        } catch (TrainingRemovalException e) {
            String reason = translator.toLocale("exception.group.training.remove");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (EmailSendingException e) {
            String reason = translator.toLocale("exception.email.sending");
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
