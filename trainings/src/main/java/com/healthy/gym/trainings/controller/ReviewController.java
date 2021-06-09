package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.NotAuthorizedClientException;
import com.healthy.gym.trainings.exception.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.RestException;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final GroupTrainingService groupTrainingsService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(GroupTrainingService groupTrainingsService,
                            ReviewService reviewService) {
        this.groupTrainingsService = groupTrainingsService;
        this.reviewService = reviewService;
    }

    //TODO only logged in users
    @PostMapping
    public GroupTrainingsReviews createGroupTrainingReview(
            @Valid @RequestBody GroupTrainingReviewRequest groupTrainingsReviews,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return groupTrainingsService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch (StarsOutOfRangeException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }


    //TODO for admin paginacja, filtrowanie po datach
    @GetMapping("/{page}")
    public ResponseEntity<Map<String, Object>> getGroupTrainingReviews(
            @RequestParam final String startDate,
            @RequestParam final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final int page) throws ParseException {

        List<GroupTrainingsReviews> reviews = new ArrayList<GroupTrainingsReviews>();
        Pageable paging = PageRequest.of(page, size);

        Page<GroupTrainingsReviews> pageReviews = reviewService.getGroupTrainingReviews(startDate,
                endDate, paging);

        reviews = pageReviews.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("tutorials", reviews);
        response.put("currentPage", pageReviews.getNumber());
        response.put("totalItems", pageReviews.getTotalElements());
        response.put("totalPages", pageReviews.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
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
            @Valid @RequestBody final GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
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
