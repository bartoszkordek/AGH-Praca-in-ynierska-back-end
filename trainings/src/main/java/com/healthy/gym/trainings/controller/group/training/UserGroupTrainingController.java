package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.dto.GroupTrainingDTO;
import com.healthy.gym.trainings.validation.ValidDateFormat;
import com.healthy.gym.trainings.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.userIsInBasicList;

@RestController
@RequestMapping(
        value = "/group",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class UserGroupTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";
    private static final String EXCEPTION_NOT_FOUND_USER_ID = "exception.not.found.user.id";

    private final Translator translator;
    private final UserGroupTrainingService userGroupTrainingService;

    @Autowired
    public UserGroupTrainingController(
            Translator translator,
            UserGroupTrainingService userGroupTrainingService
    ) {
        this.translator = translator;
        this.userGroupTrainingService = userGroupTrainingService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @GetMapping("/trainings/{userId}")
    public List<GroupTrainingDTO> getAllGroupTrainingsByUserId(
            @PathVariable @ValidIDFormat String userId,
            @RequestParam @ValidDateFormat String startDate,
            @RequestParam @ValidDateFormat String endDate
    ) {
        try {
            return userGroupTrainingService.getMyAllTrainings(userId, startDate, endDate);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @PostMapping("/{trainingId}/enroll")
    public ResponseEntity<GroupTrainingResponse> enrollToGroupTraining(
            @PathVariable @ValidIDFormat String trainingId,
            @RequestParam("clientId") @ValidIDFormat String userId
    ) {
        try {
            GroupTrainingDTO enrolledTraining
                    = userGroupTrainingService.enrollToGroupTraining(trainingId, userId);
            String message = getProperMessage(enrolledTraining, userId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GroupTrainingResponse(message, enrolledTraining));

        } catch (NotExistingGroupTrainingException exception) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date.enrollment");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserAlreadyEnrolledToTrainingException exception) {
            String reason = translator.toLocale("exception.user.already.enrolled.to.training");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    private String getProperMessage(GroupTrainingDTO enrolledTraining, String userId) {
        if (userIsInBasicList(enrolledTraining, userId)) return translator.toLocale("enrollment.success.basic.list");
        return translator.toLocale("enrollment.success.reserve.list");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @DeleteMapping("/{trainingId}/enroll")
    public ResponseEntity<GroupTrainingResponse> removeGroupTrainingEnrollment(
            @PathVariable @ValidIDFormat String trainingId,
            @RequestParam("clientId") @ValidIDFormat String userId
    ) {
        try {
            GroupTrainingDTO removedEnrolmentTraining =
                    userGroupTrainingService.removeGroupTrainingEnrollment(trainingId, userId);
            String message = translator.toLocale("enrollment.remove");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new GroupTrainingResponse(message, removedEnrolmentTraining));

        } catch (NotExistingGroupTrainingException exception) {
            String reason = translator.toLocale("exception.group.training.not.found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (PastDateException exception) {
            String reason = translator.toLocale("exception.past.date.enrollment.remove");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (UserNotFoundException exception) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_USER_ID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (TrainingEnrollmentException exception) {
            String reason = translator.toLocale("exception.group.training.enrollment.remove");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
