package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.TrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SignedClientTrainingsController {

    TrainingsService trainingsService;
    IndividualTrainingsService individualTrainingsService;

    public SignedClientTrainingsController(TrainingsService trainingsService, IndividualTrainingsService individualTrainingsService){
        this.trainingsService = trainingsService;
        this.individualTrainingsService = individualTrainingsService;
    }

    @PostMapping("/group/{trainingId}/enroll")
    public void enrollToGroupTraining(@PathVariable("trainingId") final String trainingId,
                                      @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    @PostMapping("/group/{trainingId}/reservelist/add")
    public void addToReserveList(@PathVariable("trainingId") final String trainingId,
                                 @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(@PathVariable("trainingId") final String trainingId,
                                              @RequestParam(required = true) final String clientId) throws RestException {
        try{
            trainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/group/reviews/all")
    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return trainingsService.getGroupTrainingReviews();
    }

    @GetMapping("/group/reviews/{reviewId}")
    public GroupTrainingsReviews getGroupTrainingReviewById(@PathVariable("reviewId") final String reviewId) throws RestException {
        try{
            return trainingsService.getGroupTrainingReviewById(reviewId);
        } catch (NotExistingGroupTrainingReviewException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/group/review")
    public GroupTrainingsReviews createGroupTrainingReview(@Valid @RequestBody GroupTrainingsReviewsModel groupTrainingsReviews,
                                                           @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return trainingsService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch(StarsOutOfRangeException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/review/{reviewId}")
    public GroupTrainingsReviews removeGroupTrainingReview(@PathVariable("reviewId") final String reviewId,
                                                           @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return trainingsService.removeGroupTrainingReview(reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e){
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }

    @PutMapping("/group/review/{reviewId}")
    public GroupTrainingsReviews updateGroupTrainingReview(@Valid @RequestBody final GroupTrainingsReviewsUpdateModel groupTrainingsReviewsUpdateModel,
                                                           @PathVariable("reviewId") final String reviewId,
                                                           @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return trainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel, reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException | StarsOutOfRangeException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e){
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }

    @PostMapping("/individual/request")
    public IndividualTrainings createIndividualTrainingRequest(@Valid @RequestBody final IndividualTrainingsRequestModel individualTrainingsRequestModel,
                                                               @RequestParam(required = true) final String clientId){
        return individualTrainingsService.createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
    }
}
