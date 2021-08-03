package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidDateException;
import com.healthy.gym.trainings.exception.invalid.InvalidHourException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.response.GroupTrainingParticipantsResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingsPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingsResponse;
import com.healthy.gym.trainings.shared.UserDTO;
import com.healthy.gym.trainings.service.group.training.GroupTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/public")
    public GroupTrainingsPublicResponse getPublicGroupTrainings(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate
    ) {
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

    //Temporary commented
    /*@GetMapping("/public/type/{trainingTypeId}")
    public List<GroupTrainingsPublicResponse> getPublicGroupTrainingsByType(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate
    ) {
        try {
            return groupTrainingsService.getGroupTrainingsPublicByType(trainingTypeId, startDate, endDate);

        } catch (InvalidHourException | InvalidDateException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingTypeNotFoundException e) {
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
    }*/

    @GetMapping
    public GroupTrainingsResponse getGroupTrainings(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate
    ) {
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
            @PathVariable("trainingId") final String trainingId
    ) {
        try {
            return groupTrainingsService.getGroupTrainingById(trainingId);

        } catch (InvalidHourException | InvalidDateException e) {
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

    //Temporary commented
    /*@GetMapping("/type/{trainingTypeId}")
    public List<GroupTrainingsResponse> getGroupTrainingsByType(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate
    ) {
        try {
            return groupTrainingsService.getGroupTrainingsByType(trainingTypeId, startDate, endDate);

        } catch (InvalidHourException | InvalidDateException | ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (TrainingTypeNotFoundException e) {
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
    }*/

    @GetMapping("/{trainingId}/participants")
    public GroupTrainingParticipantsResponse getTrainingParticipants(
            @PathVariable("trainingId") final String trainingId
    ) {
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
