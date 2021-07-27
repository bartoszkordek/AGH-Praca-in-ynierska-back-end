package com.healthy.gym.trainings.controller.groupTrainingControler;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
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
public class GroupTrainingUserController {

    private final Translator translator;
    private final GroupTrainingService groupTrainingsService;

    @Autowired
    public GroupTrainingUserController(Translator translator, GroupTrainingService groupTrainingsService) {
        this.translator = translator;
        this.groupTrainingsService = groupTrainingsService;
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

    @GetMapping("/public/type/{trainingTypeId}")
    public List<GroupTrainingPublicResponse> getPublicGroupTrainingsByType(
            @PathVariable("trainingTypeId") final String trainingTypeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate) throws InvalidDateException, NotExistingGroupTrainingException, StartDateAfterEndDateException, InvalidHourException, ParseException {

        try {
            return groupTrainingsService.getGroupTrainingsPublicByType(trainingTypeId, startDate, endDate);

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
