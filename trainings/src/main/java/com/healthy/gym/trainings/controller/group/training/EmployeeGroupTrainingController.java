package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.service.group.training.EmployeeGroupTrainingService;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/group", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class EmployeeGroupTrainingController {

    private final Translator translator;
    private final EmployeeGroupTrainingService employeeGroupTrainingService;

    @Autowired
    public EmployeeGroupTrainingController(
            Translator translator,
            EmployeeGroupTrainingService employeeGroupTrainingService
    ) {
        this.translator = translator;
        this.employeeGroupTrainingService = employeeGroupTrainingService;
    }

    @GetMapping("/{trainingId}/participants")
    public ParticipantsDTO getTrainingParticipants(@PathVariable @ValidIDFormat String trainingId) {
        try {
            return employeeGroupTrainingService.getTrainingParticipants(trainingId);

        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{trainingId}")
    public GroupTrainingDTO getGroupTrainingById(@PathVariable @ValidIDFormat String trainingId) {
        try {
            return employeeGroupTrainingService.getGroupTrainingById(trainingId);

        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
