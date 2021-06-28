package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.data.repository.TrainingTypeDAO;
import com.healthy.gym.trainings.exception.*;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewUpdateRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewDAO reviewRepository;
    private final TrainingTypeDAO trainingTypeRepository;
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private String defaultStartDate = "1900-01-01";
    private String defaultEndDate = "2099-12-31";

    public ReviewServiceImpl(ReviewDAO reviewRepository, TrainingTypeDAO trainingTypeRepository){
        this.reviewRepository = reviewRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public GroupTrainingReviewResponse createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviewsModel, String clientId) throws StarsOutOfRangeException {
        if (groupTrainingsReviewsModel.getStars() < 1 || groupTrainingsReviewsModel.getStars() > 5) {
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }

        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);

        String reviewId = UUID.randomUUID().toString();
        GroupTrainingsReviews dbResponse = reviewRepository.insert(new GroupTrainingsReviews(
                reviewId,
                groupTrainingsReviewsModel.getTrainingName(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        ));

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(
                    dbResponse.getReviewId(),
                    dbResponse.getTrainingName(),
                    dbResponse.getClientId(),
                    dbResponse.getDate(),
                    dbResponse.getStars(),
                    dbResponse.getText());

        return response;
    }

    @Override
    public GroupTrainingReviewResponse getReviewByReviewId(String reviewId) throws NotExistingGroupTrainingReviewException {
        if(!reviewRepository.existsByReviewId(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Training does not exist");
        }
        return reviewRepository.getFirstByReviewId(reviewId);
    }

    @Override
    public Page<GroupTrainingReviewResponse> getAllReviews(String startDate, String endDate, Pageable pageable)
            throws ParseException, StartDateAfterEndDateException {
        if(startDate == null)
            startDate = defaultStartDate;

        if(endDate == null)
            endDate = defaultEndDate;

        Date startDateParsed = sdfDate.parse(startDate);
        Date startDateMinusOneDay = new Date(startDateParsed.getTime() - (1000 * 60 * 60 * 24));
        Date endDateParsed = sdfDate.parse(endDate);
        Date endDatePlusOneDay = new Date(endDateParsed.getTime() + (1000 * 60 * 60 * 24));
        if(startDateParsed.after(endDateParsed)){
            throw new StartDateAfterEndDateException("Start date after end date");
        }

        String startDateMinusOneDayFormatted = sdfDate.format(startDateMinusOneDay);
        String endDatePlusOneDayFormatted = sdfDate.format(endDatePlusOneDay);

        return reviewRepository.findByDateBetween(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, pageable);
    }

    @Override
    public Page<GroupTrainingReviewResponse> getAllReviewsByUserId(String startDate, String endDate, String userId, Pageable pageable)
            throws ParseException, StartDateAfterEndDateException, InvalidUserIdException {
        if(startDate == null)
            startDate = defaultStartDate;

        if(endDate == null)
            endDate = defaultEndDate;

        Date startDateParsed = sdfDate.parse(startDate);
        Date startDateMinusOneDay = new Date(startDateParsed.getTime() - (1000 * 60 * 60 * 24));
        Date endDateParsed = sdfDate.parse(endDate);
        Date endDatePlusOneDay = new Date(endDateParsed.getTime() + (1000 * 60 * 60 * 24));
        if(startDateParsed.after(endDateParsed)){
            throw new StartDateAfterEndDateException("Start date after end date");
        }
        if(userId == null){
            throw new InvalidUserIdException("Empty User ID");
        }

        String startDateMinusOneDayFormatted = sdfDate.format(startDateMinusOneDay);
        String endDatePlusOneDayFormatted = sdfDate.format(endDatePlusOneDay);
        return reviewRepository.findByDateBetweenAndClientId(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, userId, pageable);
    }

    @Override
    public Page<GroupTrainingReviewResponse> getAllReviewsByTrainingTypeId(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException, TrainingTypeNotFoundException {
        if(startDate == null)
            startDate = defaultStartDate;

        if(endDate == null)
            endDate = defaultEndDate;

        Date startDateParsed = sdfDate.parse(startDate);
        Date startDateMinusOneDay = new Date(startDateParsed.getTime() - (1000 * 60 * 60 * 24));
        Date endDateParsed = sdfDate.parse(endDate);
        Date endDatePlusOneDay = new Date(endDateParsed.getTime() + (1000 * 60 * 60 * 24));
        if(startDateParsed.after(endDateParsed)){
            throw new StartDateAfterEndDateException("Start date after end date");
        }
        if(!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)){
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }

        String startDateMinusOneDayFormatted = sdfDate.format(startDateMinusOneDay);
        String endDatePlusOneDayFormatted = sdfDate.format(endDatePlusOneDay);

        return reviewRepository.findByDateBetweenAndTrainingName(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, trainingTypeId, pageable);
    }

    @Override
    public Page<GroupTrainingReviewPublicResponse> getAllReviewsByTrainingTypeIdPublic(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException, TrainingTypeNotFoundException {
        if(startDate == null)
            startDate = defaultStartDate;

        if(endDate == null)
            endDate = defaultEndDate;

        Date startDateParsed = sdfDate.parse(startDate);
        Date startDateMinusOneDay = new Date(startDateParsed.getTime() - (1000 * 60 * 60 * 24));
        Date endDateParsed = sdfDate.parse(endDate);
        Date endDatePlusOneDay = new Date(endDateParsed.getTime() + (1000 * 60 * 60 * 24));
        if(startDateParsed.after(endDateParsed)){
            throw new StartDateAfterEndDateException("Start date after end date");
        }
        if(!trainingTypeRepository.existsByTrainingTypeId(trainingTypeId)){
            throw new TrainingTypeNotFoundException("Training type does not exist");
        }

        String startDateMinusOneDayFormatted = sdfDate.format(startDateMinusOneDay);
        String endDatePlusOneDayFormatted = sdfDate.format(endDatePlusOneDay);

        return reviewRepository.getAllByDateBetweenAndTrainingName(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted, trainingTypeId, pageable);
    }

    @Override
    public GroupTrainingReviewResponse updateGroupTrainingReviewByReviewId(
            GroupTrainingReviewUpdateRequest groupTrainingReviewUpdateRequestModel,
            String reviewId, String clientId)
            throws NotExistingGroupTrainingReviewException, NotAuthorizedClientException, StarsOutOfRangeException {
        int starsAfterUpdate = groupTrainingReviewUpdateRequestModel.getStars();
        String textAfterUpdate = groupTrainingReviewUpdateRequestModel.getText();

        if(!reviewRepository.existsByReviewId(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Training does not exist");
        }
        if(!reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId)) {
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }
        GroupTrainingsReviews existingGroupTrainingsReview = reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId);
        if (starsAfterUpdate  < 1 || starsAfterUpdate  > 5) {
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        if(!textAfterUpdate.isEmpty()){
            existingGroupTrainingsReview.setText(groupTrainingReviewUpdateRequestModel.getText());
        }
        GroupTrainingsReviews responseFromDb = reviewRepository.save(existingGroupTrainingsReview);

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(responseFromDb.getReviewId(),
                responseFromDb.getTrainingName(), responseFromDb.getClientId(), responseFromDb.getDate(),
                responseFromDb.getStars(), responseFromDb.getText());

        return response;
    }

    @Override
    public GroupTrainingReviewResponse removeGroupTrainingReviewByReviewId(String reviewId, String clientId) throws NotExistingGroupTrainingReviewException, NotAuthorizedClientException {

        if(!reviewRepository.existsByReviewId(reviewId)){
            throw new NotExistingGroupTrainingReviewException("Training does not exist");
        }
        if(!reviewRepository.existsByReviewIdAndAndClientId(reviewId, clientId)) {
            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
        }

        GroupTrainingsReviews reviewToRemove = reviewRepository.findGroupTrainingsReviewsByReviewId(reviewId);
        reviewRepository.removeById(reviewId);

        GroupTrainingReviewResponse response = new GroupTrainingReviewResponse(reviewToRemove.getReviewId(),
                reviewToRemove.getTrainingName(), reviewToRemove.getClientId(), reviewToRemove.getDate(),
                reviewToRemove.getStars(), reviewToRemove.getText());

        return response;
    }


}
