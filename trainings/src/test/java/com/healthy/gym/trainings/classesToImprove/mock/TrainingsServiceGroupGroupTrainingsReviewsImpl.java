package com.healthy.gym.trainings.classesToImprove.mock;

public class TrainingsServiceGroupGroupTrainingsReviewsImpl {
//    @Autowired
//    GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository;
//
//    public TrainingsServiceGroupGroupTrainingsReviewsImpl(
//            GroupTrainingsDbRepository groupTrainingsDbRepository,
//            GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository) {
//        super(groupTrainingsDbRepository, groupTrainingReviewsDbRepository);
//    }
//
//    @Override
//    public List<GroupTrainingsReviews> getGroupTrainingReviews() {
//        return groupTrainingReviewsDbRepository.getGroupTrainingReviews();
//    }
//
//    @Override
//    public GroupTrainingsReviews getGroupTrainingReviewById(String reviewId) throws NotExistingGroupTrainingReviewException {
//        if (!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)) {
//            throw new NotExistingGroupTrainingReviewException("Review with ID: " + reviewId + " doesn't exist");
//        }
//        return groupTrainingReviewsDbRepository.getGroupTrainingsReviewById(reviewId);
//    }
//
//    @Override
//    public GroupTrainingsReviews createGroupTrainingReview(GroupTrainingReviewRequest groupTrainingsReviewsModel,
//                                                           String clientId) throws StarsOutOfRangeException {
//        if (groupTrainingsReviewsModel.getStars() < 1 || groupTrainingsReviewsModel.getStars() > 5) {
//            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
//        }
//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
//        Date now = new Date();
//        String todayDateFormatted = sdfDate.format(now);
//        return groupTrainingReviewsDbRepository.createGroupTrainingReview(groupTrainingsReviewsModel,
//                todayDateFormatted,
//                clientId);
//    }
//
//    @Override
//    public GroupTrainingsReviews updateGroupTrainingReview(GroupTrainingReviewUpdateRequest groupTrainingsReviewsUpdateModel,
//                                                           String reviewId,
//                                                           String clientId) throws NotAuthorizedClientException, StarsOutOfRangeException, NotExistingGroupTrainingReviewException {
//        if (!groupTrainingReviewsDbRepository.isGroupTrainingsReviewExist(reviewId)) {
//            throw new NotExistingGroupTrainingReviewException("Review with ID: " + reviewId + " doesn't exist");
//        }
//        if (!groupTrainingReviewsDbRepository.isClientReviewOwner(reviewId, clientId)) {
//            throw new NotAuthorizedClientException("Client is not authorized to remove this review");
//        }
//        if (groupTrainingsReviewsUpdateModel.getStars() < 1 || groupTrainingsReviewsUpdateModel.getStars() > 5) {
//            throw new StarsOutOfRangeException("Stars must be in range: 1-5");
//        }
//        return groupTrainingReviewsDbRepository.updateGroupTrainingsReview(groupTrainingsReviewsUpdateModel, reviewId);
//    }
}
