package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.dto.BasicTrainingDTO;
import com.healthy.gym.trainings.exception.notfound.UserNextTrainingNotFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/user/{userId}")
public class UserNextTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";
    private static final String EXCEPTION_NOT_FOUND_USER_ID = "exception.not.found.user.id";
    private static final String EXCEPTION_USER_NEXT_TRAINING_NOT_FOUND = "exception.user.next.training.not.found";

    private final UserGroupTrainingService userGroupTrainingService;
    private final UserIndividualTrainingService userIndividualTrainingService;
    private final Translator translator;

    @Autowired
    public UserNextTrainingController(
            UserGroupTrainingService userGroupTrainingService,
            UserIndividualTrainingService userIndividualTrainingService,
            Translator translator
    ) {
        this.userGroupTrainingService = userGroupTrainingService;
        this.userIndividualTrainingService = userIndividualTrainingService;
        this.translator = translator;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @GetMapping("/next")
    public BasicTrainingDTO getMyNextTraining(
            @PathVariable @ValidIDFormat final String userId
    ){
        try{
            BasicTrainingDTO individual = userIndividualTrainingService.getMyNextTraining(userId);
            BasicTrainingDTO group = userGroupTrainingService.getMyNextTraining(userId);

            return getEarlierTraining(group, individual);

        } catch (
                UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (
                UserNextTrainingNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_USER_NEXT_TRAINING_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.OK, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    private BasicTrainingDTO getEarlierTraining(BasicTrainingDTO group, BasicTrainingDTO individual) throws UserNextTrainingNotFoundException {
        if(group == null && individual == null) throw new UserNextTrainingNotFoundException();
        if(group == null) return individual;
        if(individual == null) return group;
        String groupTrainingStartDate = group.getStartDate();
        String individualTrainingStartDate = individual.getStartDate();
        LocalDateTime parsedGroupTrainingStartDate = LocalDateTime
                .parse(groupTrainingStartDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime parsedIndividualTrainingStartDate = LocalDateTime
                .parse(individualTrainingStartDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if(parsedGroupTrainingStartDate.isBefore(parsedIndividualTrainingStartDate)){
            return group;
        } else {
            return individual;
        }
    }
}
