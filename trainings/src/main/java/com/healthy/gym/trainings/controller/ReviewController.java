package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.exception.invalid.InvalidUserIdException;
import com.healthy.gym.trainings.exception.notexisting.NotExistingGroupTrainingReviewException;
import com.healthy.gym.trainings.exception.notfound.TrainingTypeNotFoundException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(Translator translator,
                            ReviewService reviewService) {
        this.translator = translator;
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
    public ResponseEntity<GroupTrainingReviewResponse> createGroupTrainingReview(
            @Valid @RequestBody GroupTrainingReviewRequest groupTrainingsReviews,
            @RequestParam final String clientId
    ) {
        try {
            GroupTrainingReviewResponse response = reviewService.createGroupTrainingReview(groupTrainingsReviews, clientId);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (StarsOutOfRangeException e) {
            String reason = translator.toLocale("exception.review.stars.out.of.range");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (TrainingTypeNotFoundException e){
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
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

        } catch (ParseException e){
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e){
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
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
        } catch (ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        }  catch (InvalidUserIdException e){
            String reason = translator.toLocale("exception.not.found.user.id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
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
        } catch (ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (TrainingTypeNotFoundException e){
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
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

        } catch (ParseException e) {
            String reason = translator.toLocale("exception.date.or.hour.parse");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (StartDateAfterEndDateException e) {
            String reason = translator.toLocale("exception.start.date.after.end.date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (TrainingTypeNotFoundException e){
            String reason = translator.toLocale("exception.not.found.training.type");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    @GetMapping("/{reviewId}")
    public GroupTrainingReviewResponse getGroupTrainingReviewById(
            @PathVariable("reviewId") final String reviewId
    ) {
        try {
            return reviewService.getReviewByReviewId(reviewId);
        } catch (NotExistingGroupTrainingReviewException e) {
            String reason = translator.toLocale("exception.not.found.review.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    //TODO only user own review
    @PutMapping("/{reviewId}")
    public GroupTrainingReviewResponse updateGroupTrainingReview(
            @Valid @RequestBody final GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel,
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) {
        try {
            return reviewService.updateGroupTrainingReviewByReviewId(groupTrainingReviewUpdateRequestModel, reviewId, clientId);//groupTrainingsService.updateGroupTrainingReview(groupTrainingReviewUpdateRequestModel, reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException e) {
            String reason = translator.toLocale("exception.not.found.review.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (StarsOutOfRangeException e) {
            String reason = translator.toLocale("exception.review.stars.out.of.range");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, e);
        } catch (NotAuthorizedClientException e) {
            String reason = translator.toLocale("exception.access.denied");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }

    //TODO only admin and user own review
    @DeleteMapping("/{reviewId}")
    public GroupTrainingReviewResponse removeGroupTrainingReview(
            @PathVariable("reviewId") final String reviewId,
            @RequestParam final String clientId
    ) {
        try {
            return reviewService.removeGroupTrainingReviewByReviewId(reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException e) {
            String reason = translator.toLocale("exception.not.found.review.id");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, reason, e);
        } catch (NotAuthorizedClientException e) {
            String reason = translator.toLocale("exception.access.denied");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, reason, e);
        } catch (Exception exception) {
            String reason = translator.toLocale("exception.internal.error");
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
