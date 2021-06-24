package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
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
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    private final Translator translator;
    private final GroupTrainingService groupTrainingsService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(Translator translator, GroupTrainingService groupTrainingsService,
                            ReviewService reviewService) {
        this.translator = translator;
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

    private Map<String, Object> reviewPaginationPublicResponse(Page<GroupTrainingReviewPublicResponse> pageReviews){

        List<GroupTrainingReviewPublicResponse> reviews = pageReviews.getContent();

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
    ) {
        try {
            return reviewService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch (StarsOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    //TODO for admin
    @GetMapping("/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviews(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final int page) {

        Pageable paging = PageRequest.of(page, size);

        try{ Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviews(startDate,
                    endDate, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ParseException | StartDateAfterEndDateException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }

    // TODO only for admin and user who owns it
    @GetMapping("/user/{userId}/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByUserId(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final String userId,
            @PathVariable final int page) {

        Pageable paging = PageRequest.of(page, size);

        try { Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviewsByUserId(startDate,
                    endDate, userId, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParseException | StartDateAfterEndDateException | InvalidUserIdException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    // TODO only logged in users
    @GetMapping("/trainingType/{trainingTypeId}/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByTrainingTypeId(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final String trainingTypeId,
            @PathVariable final int page) {

        Pageable paging = PageRequest.of(page, size);

        try { Page<GroupTrainingReviewResponse> pageReviews = reviewService.getAllReviewsByTrainingTypeId(startDate,
                endDate, trainingTypeId, paging);

            Map<String, Object> response = reviewPaginationResponse(pageReviews);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParseException | StartDateAfterEndDateException | TrainingTypeNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    //TODO without usernames and avatars
    @GetMapping("/trainingType/{trainingTypeId}/public/page/{page}")
    public ResponseEntity<Map<String, Object>> getAllReviewsByTrainingTypeIdPublic(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final String endDate,
            @RequestParam(defaultValue = "10") final int size,
            @PathVariable final String trainingTypeId,
            @PathVariable final int page) {

        Pageable paging = PageRequest.of(page, size);

        try{ Page<GroupTrainingReviewPublicResponse> pageReviews = reviewService.getAllReviewsByTrainingTypeIdPublic(startDate,
                endDate, trainingTypeId, paging);

            Map<String, Object> response = reviewPaginationPublicResponse(pageReviews);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (ParseException | StartDateAfterEndDateException | TrainingTypeNotFoundException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{reviewId}")
    public GroupTrainingsReviews getGroupTrainingReviewById(
            @PathVariable("reviewId") final String reviewId
    ) {
        try {
            return groupTrainingsService.getGroupTrainingReviewById(reviewId);
        } catch (NotExistingGroupTrainingReviewException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    //TODO only user own review
    @PutMapping("/{reviewId}")
    public GroupTrainingsReviews updateGroupTrainingReview(
            @Valid @RequestBody final GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) {
        try {
            return groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel, reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException | StarsOutOfRangeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NotAuthorizedClientException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);

        }
    }

    //TODO only admin and user own review
    @DeleteMapping("/{reviewId}")
    public GroupTrainingsReviews removeGroupTrainingReview(
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) {
        try {
            return groupTrainingsService.removeGroupTrainingReview(reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (NotAuthorizedClientException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}
