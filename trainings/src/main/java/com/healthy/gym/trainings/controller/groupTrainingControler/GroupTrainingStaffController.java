package com.healthy.gym.trainings.controller.groupTrainingControler;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupTrainingStaffController {

    private final Translator translator;
    private final GroupTrainingService groupTrainingsService;

    @Autowired
    public GroupTrainingStaffController(Translator translator, GroupTrainingService groupTrainingsService) {
        this.translator = translator;
        this.groupTrainingsService = groupTrainingsService;
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

    @GetMapping("/type/{trainingTypeId}")
    public List<GroupTrainingResponse> getGroupTrainingsByType(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate) {

        try{
            return groupTrainingsService.getGroupTrainingsByType(trainingTypeId, startDate, endDate);

        } catch (InvalidHourException | InvalidDateException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingTypeNotFoundException e){
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.not.found.training.type.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{trainingId}/participants")
    public List<ParticipantsResponse> getTrainingParticipants(@PathVariable("trainingId") final String trainingId) {

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
}
