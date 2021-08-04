package com.healthy.gym.trainings.controller.group.training;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.PastDateException;
import com.healthy.gym.trainings.exception.UserAlreadyEnrolledToTrainingException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.exception.training.TrainingEnrollmentException;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.service.group.training.UserGroupTrainingService;
import com.healthy.gym.trainings.shared.GroupTrainingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.healthy.gym.trainings.utils.ParticipantsExtractor.userIsInBasicList;

@RestController
@RequestMapping("/group")
public class UserGroupTrainingController {

    private static final String EXCEPTION_INTERNAL_ERROR = "exception.internal.error";
    private static final String EXCEPTION_GROUP_TRAINING_ENROLLMENT = "exception.group.training.enrollment";
    private static final String EXCEPTION_NOT_FOUND_TRAINING_ID = "exception.not.found.training.id";
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

    @PreAuthorize("hasRole('ADMIN') or principal==#userId")
    @GetMapping("/trainings/{userId}")
    public List<GroupTrainingPublicResponse> getAllGroupTrainingsByUserId(@PathVariable final String userId) {
        try {
            return userGroupTrainingService.getMyAllTrainings(userId);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or principal==#userId")
    @PostMapping("/{trainingId}/enroll")
    public ResponseEntity<GroupTrainingResponse> enrollToGroupTraining(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam("clientId") final String userId
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
            String reason = translator.toLocale("exception.not.found.user.id");
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
    public void removeGroupTrainingEnrollment(
            @PathVariable("trainingId") final String trainingId,
            @RequestParam("clientId") final String userId
    ) {
        try {
            userGroupTrainingService.removeGroupTrainingEnrollment(trainingId, userId);

        } catch (NotExistingGroupTrainingException e) {
            String reason = translator.toLocale(EXCEPTION_NOT_FOUND_TRAINING_ID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);

        } catch (TrainingEnrollmentException e) {
            String reason = translator.toLocale(EXCEPTION_GROUP_TRAINING_ENROLLMENT);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);

        } catch (Exception exception) {
            String reason = translator.toLocale(EXCEPTION_INTERNAL_ERROR);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
