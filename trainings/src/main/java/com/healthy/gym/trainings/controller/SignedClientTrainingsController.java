package com.healthy.gym.trainings.controller;

import com.healthy.gym.trainings.entity.GroupTrainingsReviews;
import com.healthy.gym.trainings.entity.IndividualTrainings;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsModel;
import com.healthy.gym.trainings.model.GroupTrainingsReviewsUpdateModel;
import com.healthy.gym.trainings.model.IndividualTrainingsRequestModel;
import com.healthy.gym.trainings.service.IndividualTrainingsService;
import com.healthy.gym.trainings.service.GroupTrainingsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
public class SignedClientTrainingsController {

    GroupTrainingsService groupTrainingsService;
    IndividualTrainingsService individualTrainingsService;

    public SignedClientTrainingsController(GroupTrainingsService groupTrainingsService, IndividualTrainingsService individualTrainingsService){
        this.groupTrainingsService = groupTrainingsService;
        this.individualTrainingsService = individualTrainingsService;
    }

    @PostMapping("/group/{trainingId}/enroll")
    public void enrollToGroupTraining(@PathVariable("trainingId") final String trainingId,
                                      @RequestParam(required = true) final String clientId) throws RestException {
        try{
            groupTrainingsService.enrollToGroupTraining(trainingId, clientId);
        } catch (TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }

    }

    @PostMapping("/group/{trainingId}/reservelist/add")
    public void addToReserveList(@PathVariable("trainingId") final String trainingId,
                                 @RequestParam(required = true) final String clientId) throws RestException {
        try{
            groupTrainingsService.addToReserveList(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/{trainingId}/enroll")
    public void removeGroupTrainingEnrollment(@PathVariable("trainingId") final String trainingId,
                                              @RequestParam(required = true) final String clientId) throws RestException {
        try{
            groupTrainingsService.removeGroupTrainingEnrollment(trainingId, clientId);
        } catch (NotExistingGroupTrainingException | TrainingEnrollmentException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @GetMapping("/group/reviews/all")
    public List<GroupTrainingsReviews> getGroupTrainingReviews(){
        return groupTrainingsService.getGroupTrainingReviews();
    }

    @GetMapping("/group/reviews/{reviewId}")
    public GroupTrainingsReviews getGroupTrainingReviewById(@PathVariable("reviewId") final String reviewId) throws RestException {
        try{
            return groupTrainingsService.getGroupTrainingReviewById(reviewId);
        } catch (NotExistingGroupTrainingReviewException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @PostMapping("/group/review")
    public GroupTrainingsReviews createGroupTrainingReview(@Valid @RequestBody GroupTrainingsReviewsModel groupTrainingsReviews,
                                                           @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return groupTrainingsService.createGroupTrainingReview(groupTrainingsReviews, clientId);
        } catch(StarsOutOfRangeException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/group/review/{reviewId}")
    public GroupTrainingsReviews removeGroupTrainingReview(@PathVariable("reviewId") final String reviewId,
                                                           @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return groupTrainingsService.removeGroupTrainingReview(reviewId, clientId);
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
            return groupTrainingsService.updateGroupTrainingReview(groupTrainingsReviewsUpdateModel, reviewId, clientId);
        } catch (NotExistingGroupTrainingReviewException | StarsOutOfRangeException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e){
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }

    @PostMapping("/individual/request")
    public IndividualTrainings createIndividualTrainingRequest(@Valid @RequestBody final IndividualTrainingsRequestModel individualTrainingsRequestModel,
                                                               @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return individualTrainingsService.createIndividualTrainingRequest(individualTrainingsRequestModel, clientId);
        } catch (InvalidHourException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        }
    }

    @DeleteMapping("/individual/request/{trainingId}")
    public IndividualTrainings cancelIndividualTrainingRequest(@PathVariable("trainingId") final String trainingId,
                                                @RequestParam(required = true) final String clientId) throws RestException {
        try{
            return individualTrainingsService.cancelIndividualTrainingRequest(trainingId, clientId);
        } catch (NotExistingIndividualTrainingException | ParseException | RetroIndividualTrainingException e){
            throw new RestException(e.getMessage(), HttpStatus.BAD_REQUEST, e);
        } catch (NotAuthorizedClientException e){
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN, e);
        }
    }

}
