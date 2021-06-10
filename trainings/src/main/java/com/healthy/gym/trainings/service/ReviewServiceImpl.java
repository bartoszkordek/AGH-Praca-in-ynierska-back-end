package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
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
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private String defaultStartDate = "1900-01-01";
    private String defaultEndDate = "2099-12-31";

    public ReviewServiceImpl(ReviewDAO reviewRepository){
        this.reviewRepository = reviewRepository;
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
                    dbResponse.getDate(),
                    dbResponse.getClientId(),
                    dbResponse.getStars(),
                    dbResponse.getText());

        return response;
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

        return reviewRepository.findByDateBetween(sdfDate.format(startDateMinusOneDay),
                sdfDate.format(endDatePlusOneDay), pageable);
    }

    @Override
    public Page<GroupTrainingReviewResponse> getAllReviewsByUserId(String startDate, String endDate, String userId, Pageable pageable)
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
        return reviewRepository.findByDateBetweenAndClientId(sdfDate.format(startDateMinusOneDay),
                sdfDate.format(endDatePlusOneDay), userId, pageable);
    }

    @Override
    public Page<GroupTrainingReviewResponse> getAllReviewsByTrainingTypeId(String startDate, String endDate, String trainingTypeId, Pageable pageable) throws ParseException, StartDateAfterEndDateException {
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
        return reviewRepository.findByDateBetweenAndTrainingName(sdfDate.format(startDateMinusOneDay),
                sdfDate.format(endDatePlusOneDay), trainingTypeId, pageable);
    }


}
