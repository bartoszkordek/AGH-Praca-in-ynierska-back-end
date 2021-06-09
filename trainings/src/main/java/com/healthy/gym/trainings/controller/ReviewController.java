package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final GroupTrainingsService groupTrainingsService;

    @Autowired
    public ReviewController(GroupTrainingsService groupTrainingsService) {
        this.groupTrainingsService = groupTrainingsService;
    }

    //TODO only logged in users
    @PostMapping
    public GroupTrainingsReviews createGroupTrainingReview(
            @Valid @RequestBody GroupTrainingsReviewsModel groupTrainingsReviews,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return groupTrainingsService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch (StarsOutOfRangeException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO for admin paginacja, filtrowanie po datach
    @GetMapping
    public List<GroupTrainingsReviews> getGroupTrainingReviews() {
        return groupTrainingsService.getGroupTrainingReviews();
    }

    // TODO only for admin and user who owns it
    @GetMapping("/user/{userId}")
    public String getAllReviewsByUserId(@PathVariable final String userId) {
        return null;
    }

    // TODO only logged in users
    @GetMapping("/trainingType/{trainingTypeId}")
    public String getAllReviewsByTrainingTypeId(@PathVariable final String trainingTypeId) {
        return null;
    }

    //TODO without usernames and avatars
    @GetMapping("/trainingType/{trainingTypeId}/public")
    public String getAllReviewsByTrainingTypeIdPublic(@PathVariable final String trainingTypeId) {
        return null;
    }

    @GetMapping("/{reviewId}")
    public GroupTrainingsReviews getGroupTrainingReviewById(
            @PathVariable("reviewId") final String reviewId
    ) throws RestException {
        try {
            return groupTrainingsService.getGroupTrainingReviewById(reviewId);
        } catch (NotExistingGroupTrainingReviewException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    //TODO only user own review
    @PutMapping("/{reviewId}")
    public GroupTrainingsReviews updateGroupTrainingReview(
            @Valid @RequestBody final GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel,
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel, reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException | StarsOutOfRangeException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e) {
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }

    //TODO only admin and user own review
    @DeleteMapping("/{reviewId}")
    public GroupTrainingsReviews removeGroupTrainingReview(
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return groupTrainingsService.removeGroupTrainingReview(reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e) {
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }
}
