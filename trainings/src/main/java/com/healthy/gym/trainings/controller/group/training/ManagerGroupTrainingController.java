package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.ResponseBindException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.LocationNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainerNotFoundException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.exception.occupied.LocationOccupiedException;
import com.healthy.gym.trainings.exception.occupied.TrainerOccupiedException;
import com.healthy.gym.trainings.model.request.ManagerGroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.group.training.ManagerGroupTrainingService;
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
    public ResponseEntity<GroupTrainingResponse> createGroupTraining(
            @Valid @RequestBody final ManagerGroupTrainingRequest createGroupTrainingRequest,
            final BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            GroupTrainingDTO createdTraining = managerGroupTrainingService
                    .createGroupTraining(createGroupTrainingRequest);
            String message = translator.toLocale("request.create.training.success");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new GroupTrainingResponse(message, createdTraining));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (LocationOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.location.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
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
            String reason = translator.toLocale("exception.create.group.training.trainingType.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<GroupTrainingResponse> updateGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody final ManagerGroupTrainingRequest groupTrainingRequest,
            final BindingResult bindingResult
    ) throws ResponseBindException {
        try {
            if (bindingResult.hasErrors()) throw new BindException(bindingResult);

            GroupTrainingDTO updateGroupTraining = managerGroupTrainingService
                    .updateGroupTraining(trainingId, groupTrainingRequest);
            String message = translator.toLocale("request.update.training.success");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GroupTrainingResponse(message, updateGroupTraining));

        } catch (BindException exception) {
            String reason = translator.toLocale("request.bind.exception");
            throw new ResponseBindException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationNotFoundException exception) {
            String reason = translator.toLocale("exception.location.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (LocationOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.location.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NotExistingGroupTrainingException exception) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerNotFoundException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainerOccupiedException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainer.occupied");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainingTypeNotFoundException exception) {
            String reason = translator.toLocale("exception.create.group.training.trainingType.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<GroupTrainingResponse> removeGroupTraining(
            @PathVariable("trainingId") final String trainingId
    ) {
        try {
            GroupTrainingDTO updateGroupTraining = managerGroupTrainingService.removeGroupTraining(trainingId);
            String message = translator.toLocale("request.delete.training.success");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GroupTrainingResponse(message, updateGroupTraining));

        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
