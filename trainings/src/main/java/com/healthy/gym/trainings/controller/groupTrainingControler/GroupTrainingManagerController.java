package com.healthy.gym.trainings.controller.groupTrainingControler;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/group")
public class GroupTrainingManagerController {

    private final Translator translator;
    private final GroupTrainingService groupTrainingsService;

    @Autowired
    public GroupTrainingManagerController(Translator translator, GroupTrainingService groupTrainingsService) {
        this.translator = translator;
        this.groupTrainingsService = groupTrainingsService;
    }

    // TODO only manager
    @PostMapping
    public GroupTrainingResponse createGroupTraining(
            @Valid @RequestBody GroupTrainingRequest groupTrainingModel){

        try {
            return groupTrainingsService.createGroupTraining(groupTrainingModel);

        } catch (InvalidHourException | InvalidDateException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingCreationException e) {
            String reason = translator.toLocale("exception.group.training.create");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }

    }

    // TODO only manager
    @PutMapping("/{trainingId}")
    public GroupTrainingResponse updateGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody GroupTrainingRequest groupTrainingModelRequest ) {

        try {
            return groupTrainingsService.updateGroupTraining(trainingId, groupTrainingModelRequest);

        } catch (InvalidHourException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingUpdateException e ) {
            String reason = translator.toLocale("exception.group.training.update");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (EmailSendingException e) {
            String reason = translator.toLocale("exception.email.sending");
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    // TODO only manager
    @DeleteMapping("/{trainingId}")
    public GroupTrainingResponse removeGroupTraining(@PathVariable("trainingId") final String trainingId) {

        try {
            return groupTrainingsService.removeGroupTraining(trainingId);

        } catch (TrainingRemovalException e) {
            String reason = translator.toLocale("exception.group.training.remove");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (EmailSendingException e){
            String reason = translator.toLocale("exception.email.sending");
            throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
