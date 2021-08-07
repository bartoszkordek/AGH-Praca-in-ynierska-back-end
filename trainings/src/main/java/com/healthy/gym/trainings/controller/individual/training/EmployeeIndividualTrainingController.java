package com.healthy.gym.trainings.controller.individual.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingIndividualTrainingException;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.service.individual.training.EmployeeIndividualTrainingService;
import com.healthy.gym.trainings.validation.ValidDateFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import com.healthy.gym.trainings.validation.ValidPageNumber;
import com.healthy.gym.trainings.validation.ValidPageSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
@RequestMapping(value = "/individual/employee", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class EmployeeIndividualTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";

    private final EmployeeIndividualTrainingService individualTrainingsService;
    private final Translator translator;

    @Autowired
    public EmployeeIndividualTrainingController(
            EmployeeIndividualTrainingService individualTrainingsService,
            Translator translator
    ) {
        this.individualTrainingsService = individualTrainingsService;
        this.translator = translator;
    }

    @GetMapping("/{trainingId}")
    public IndividualTrainingDTO getIndividualTrainingById(@PathVariable @ValidIDFormat final String trainingId) {
        try {
            return individualTrainingsService.getIndividualTrainingById(trainingId);

        } catch (NotExistingIndividualTrainingException exception) {
            String reason = translator.toLocale("exception.not.existing.individual.training");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping
    public List<IndividualTrainingDTO> getAllIndividualTrainingRequests(
            @RequestParam @ValidDateFormat String startDate,
            @RequestParam @ValidDateFormat String endDate,
            @RequestParam @ValidPageNumber int pageNumber,
            @RequestParam @ValidPageSize int pageSize
    ) {
        try {
            return individualTrainingsService
                    .getIndividualTrainings(startDate, endDate, pageNumber, pageSize);

        } catch (NoIndividualTrainingFoundException exception) {
            String reason = translator.toLocale("exception.no.individual.training.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/accepted")
    public List<IndividualTrainingDTO> getAllAcceptedIndividualTrainingRequests(
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate,
            @RequestParam @ValidPageNumber final int pageNumber,
            @RequestParam @ValidPageSize final int pageSize
    ) {
        try {
            return individualTrainingsService
                    .getAllAcceptedIndividualTrainings(startDate, endDate, pageNumber, pageSize);

        } catch (NoIndividualTrainingFoundException exception) {
            String reason = translator.toLocale("exception.no.individual.training.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }


}
