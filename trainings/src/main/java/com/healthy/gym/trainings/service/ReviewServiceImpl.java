package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.repository.ReviewDAO;
import com.healthy.gym.trainings.exception.StarsOutOfRangeException;
import com.healthy.gym.trainings.model.request.GroupTrainingReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReviewServiceImpl implements ReviewService{

    private final ReviewDAO reviewRepository;

    public ReviewServiceImpl(ReviewDAO reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    @Override
    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviewsModel, String clientId) throws StarsOutOfRangeException {
        if (groupTrainingsReviewsModel.getStars() < 1 || groupTrainingsReviewsModel.getStars() > 5) {
            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
        }
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);

        GroupTrainingsReviews response = reviewRepository.insert(new GroupTrainingsReviews(
                groupTrainingsReviewsModel.getTrainingName(),
                clientId,
                todayDateFormatted,
                groupTrainingsReviewsModel.getStars(),
                groupTrainingsReviewsModel.getText()
        ));
        return response;
    }

    @Override
    public Page<GroupTrainingsReviews> getGroupTrainingReviews(String startDate, String endDate, Pageable pageable) throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateParsed = sdfDate.parse(startDate);
        Date startDateMinusOneDay = new Date(startDateParsed.getTime() - (1000 * 60 * 60 * 24));
        Date endDateParsed = sdfDate.parse(endDate);
        Date endDatePlusOneDay = new Date(endDateParsed.getTime() + (1000 * 60 * 60 * 24));
        return reviewRepository.findAllByDateAfterAndDateBefore(sdfDate.format(startDateMinusOneDay),
                sdfDate.format(endDatePlusOneDay), pageable);
    }


}
