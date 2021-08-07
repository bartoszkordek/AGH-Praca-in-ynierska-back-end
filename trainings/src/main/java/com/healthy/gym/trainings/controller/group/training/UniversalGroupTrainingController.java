package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.GroupTrainingWithoutParticipantsDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.service.group.training.UniversalGroupTrainingService;
import com.healthy.gym.trainings.validation.ValidDateFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/group", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UniversalGroupTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";
    private static final String EXCEPTION_START_DATE_AFTER_END_DATE = "exception.start.date.after.end.date";

    private final Translator translator;
    private final UniversalGroupTrainingService groupTrainingsService;

    @Autowired
    public UniversalGroupTrainingController(
            Translator translator,
            UniversalGroupTrainingService groupTrainingsService
    ) {
        this.translator = translator;
        this.groupTrainingsService = groupTrainingsService;
    }

    @GetMapping("/public")
    public List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsWithoutParticipants(
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return groupTrainingsService.getGroupTrainingsWithoutParticipants(startDate, endDate);

        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale(EXCEPTION_START_DATE_AFTER_END_DATE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<GroupTrainingDTO> getGroupTrainingsWithParticipants(
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return groupTrainingsService.getGroupTrainingsWithParticipants(startDate, endDate);

        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale(EXCEPTION_START_DATE_AFTER_END_DATE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/public/type/{trainingTypeId}")
    public List<GroupTrainingWithoutParticipantsDTO> getGroupTrainingsByTypeWithoutParticipants(
            @PathVariable @ValidIDFormat final String trainingTypeId,
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return groupTrainingsService
                    .getGroupTrainingsByTypeWithoutParticipants(trainingTypeId, startDate, endDate);

        } catch (TrainingTypeNotFoundException e) {
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale(EXCEPTION_START_DATE_AFTER_END_DATE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/type/{trainingTypeId}")
    public List<GroupTrainingDTO> getGroupTrainingsByTypeWithParticipants(
            @PathVariable @ValidIDFormat final String trainingTypeId,
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return groupTrainingsService.getGroupTrainingsByTypeWithParticipants(trainingTypeId, startDate, endDate);

        } catch (TrainingTypeNotFoundException e) {
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale(EXCEPTION_START_DATE_AFTER_END_DATE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
