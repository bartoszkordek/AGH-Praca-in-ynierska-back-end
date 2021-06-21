package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.GroupTrainingService;
import com.healthy.gym.trainings.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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

    private Map<String, Object> reviewPaginationResponse(Page<GroupTrainingReviewResponse> pageReviews){

        List<GroupTrainingReviewResponse> reviews = pageReviews.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("tutorials", reviews);
        response.put("currentPage", pageReviews.getNumber());
        response.put("totalItems", pageReviews.getTotalElements());
        response.put("totalPages", pageReviews.getTotalPages());

        return response;
    }

    //TODO only logged in users
    @PostMapping
    public GroupTrainingReviewResponse createGroupTrainingReview(
            @Valid @RequestBody GroupTrainingReviewRequest groupTrainingsReviews,
            @RequestParam final String clientId
    ) throws RestException {
        try {
            return reviewService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch (StarsOutOfRangeException e) {
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }


    //TODO for admin paginacja, filtrowanie po datach
    @GetMapping("/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviews(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final int page) throws RestException {

        Pageable paging = PageRequest.of(page, size);

        try{ Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviews(startDate,
                    endDate, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ParseException | StartDateAfterEndDateException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    // TODO only for admin and user who owns it
    @GetMapping("/user/{userId}/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByUserId(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final String userId,
            @PathVariable final int page) throws RestException {

        Pageable paging = PageRequest.of(page, size);

        try { Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviewsByUserId(startDate,
                    endDate, userId, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParseException | StartDateAfterEndDateException | InvalidUserIdException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    // TODO only logged in users
    @GetMapping("/trainingType/{trainingTypeId}/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByTrainingTypeId(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final String trainingTypeId,
            @PathVariable final int page) throws RestException {

        Pageable paging = PageRequest.of(page, size);

        try { Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviewsByTrainingTypeId(startDate,
                endDate, trainingTypeId, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParseException | StartDateAfterEndDateException | TrainingTypeNotFoundException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
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
