package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupTrainingController {

    private final GroupTrainingService groupTrainingsService;

    @Autowired
    public GroupTrainingController(GroupTrainingService groupTrainingsService) {
        this.groupTrainingsService = groupTrainingsService;
    }

    // TODO only manager
    @PostMapping
    public GroupTrainings createGroupTraining(
            @Valid @RequestBody GroupTrainingRequest groupTrainingModel
    ) throws RestException {
        try {
            return groupTrainingsService.createGroupTraining(groupTrainingModel);
        } catch (TrainingCreationException | InvalidHourException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping
    public List<GroupTrainingResponse> getGroupTrainings() throws InvalidHourException {
        return groupTrainingsService.getGroupTrainings();
    }

    @GetMapping("/public")
    public List<GroupTrainingPublicResponse> getPublicGroupTrainings()
            throws InvalidHourException, InvalidDateException {
        return groupTrainingsService.getPublicGroupTrainings();
    }

    @GetMapping("/{trainingId}")
    public GroupTrainingResponse getGroupTrainingById(
            @PathVariable("trainingId") final String trainingId
    ) throws NotExistingGroupTrainingException, InvalidHourException {
        return groupTrainingsService.getGroupTrainingById(trainingId);
    }

    @GetMapping("/{trainingId}/participants")
    public List<String> getTrainingParticipants(@PathVariable("trainingId") final String trainingId)
            throws RestException {
        try {
            return groupTrainingsService.getTrainingParticipants(trainingId);
        } catch (NotExistingGroupTrainingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only manager
    @PutMapping("/{trainingId}")
    public GroupTrainings updateGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @Valid @RequestBody GroupTrainingRequest groupTrainingModelRequest
    ) throws RestException {
        try {
            return groupTrainingsService.updateGroupTraining(trainingId, groupTrainingModelRequest);
        } catch (TrainingUpdateException | InvalidHourException | EmailSendingException | ParseException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only manager
    @DeleteMapping("/{trainingId}")
    public GroupTrainings removeGroupTraining(@PathVariable("trainingId") final String trainingId)
            throws RestException {
        try {
            return groupTrainingsService.removeGroupTraining(trainingId);
        } catch (TrainingRemovalException | EmailSendingException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only logged in users and ADMIN, dodać po ID
    @GetMapping("/trainings/{userId}")
    public List<GroupTrainingPublicResponse> getAllGroupTrainingsByUserId(@PathVariable final String userId)
            throws InvalidDateException, InvalidHourException {
        return groupTrainingsService.getMyAllTrainings(userId);
    }

    //TODO only with USER ROLE
    @PostMapping("/{trainingId}/enroll")
    public void enrollToGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            groupTrainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    @PostMapping("/{trainingId}/reservelist/add")
    public void addToReserveList(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam(required = true) final String clientId
    ) throws RestException {
        try {
            groupTrainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO aktualizacja listy podstawowej i listy rezerwowej
    @DeleteMapping("/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            groupTrainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }
}
