package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupTrainingController {

    private final Translator translator;
    private final GroupTrainingService groupTrainingsService;

    @Autowired
    public GroupTrainingController(Translator translator, GroupTrainingService groupTrainingsService) {
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

    @GetMapping
    public List<GroupTrainingResponse> getGroupTrainings(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate) {
        try {
            return groupTrainingsService.getGroupTrainings(startDate, endDate);
        } catch (InvalidDateException | InvalidHourException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/public")
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate) {
        try {
            return groupTrainingsService.getPublicGroupTrainings(startDate, endDate);
        } catch (InvalidDateException | InvalidHourException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{trainingId}")
    public GroupTrainingResponse getGroupTrainingById(
            @PathVariable("trainingId") final String trainingId) {
        try{
            return groupTrainingsService.getGroupTrainingById(trainingId);
        } catch (InvalidHourException | InvalidDateException e){
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.not.found.training.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{trainingId}/participants")
    public List<String> getTrainingParticipants(@PathVariable("trainingId") final String trainingId) {
        try {
            return groupTrainingsService.getTrainingParticipants(trainingId);
        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.not.found.training.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
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

    // TODO only logged in users and ADMIN, dodać po ID
    @GetMapping("/trainings/{userId}")
    public List<GroupTrainingPublicResponse> getAllGroupTrainingsByUserId(@PathVariable final String userId) {
        try {
            return groupTrainingsService.getMyAllTrainings(userId);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    //TODO only with USER ROLE
    @PostMapping("/{trainingId}/enroll")
    public void enrollToGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId) {
        try {
            groupTrainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e) {
            String reason = translator.toLocale("exception.group.training.enrollment");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }

    }

    @PostMapping("/{trainingId}/reservelist/add")
    public void addToReserveList(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam(required = true) final String clientId) {
        try {
            groupTrainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.not.found.training.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (TrainingEnrollmentException e) {
            String reason = translator.toLocale("exception.group.training.enrollment");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    //TODO aktualizacja listy podstawowej i listy rezerwowej
    @DeleteMapping("/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId) {
        try {
            groupTrainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.not.found.training.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (TrainingEnrollmentException e) {
            String reason = translator.toLocale("exception.group.training.enrollment");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
