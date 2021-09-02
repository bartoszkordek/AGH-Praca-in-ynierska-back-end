package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.GenericTrainingDTO;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.notfound.NoTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.TrainerService;
import com.healthy.gym.trainings.validation.ValidDateFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('TRAINER')")
@RequestMapping("/trainer/{userId}/trainings")
public class TrainerController {

    private final TrainerService trainerService;
    private final Translator translator;

    public TrainerController(TrainerService trainerService, Translator translator) {
        this.trainerService = trainerService;
        this.translator = translator;
    }

    @GetMapping
    public List<GenericTrainingDTO> getAllTrainerTrainings(
            @PathVariable @ValidIDFormat final String userId,
            @RequestParam @ValidDateFormat final String startDate,
            @RequestParam @ValidDateFormat final String endDate
    ) {
        try {
            return trainerService.getAllTrainerTrainings(userId, startDate, endDate);

        } catch (NoTrainingFoundException exception) {
            String reason = translator.toLocale("exception.no.training.found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
