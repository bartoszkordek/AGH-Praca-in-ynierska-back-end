package com.healthy.gym.trainings.classesToImprove.mock;

import org.junit.jupiter.api.Disabled;

@Disabled
public class TrainingsServiceGroupGroupTrainingsImpl {
//
//    @Autowired
//    GroupTrainingsDbRepository groupTrainingsDbRepository;
//
//    public TrainingsServiceGroupGroupTrainingsImpl(
//            GroupTrainingsDbRepository groupTrainingsDbRepository,
//            GroupTrainingReviewsDbRepository groupTrainingReviewsDbRepository) {
//        super(groupTrainingsDbRepository, groupTrainingReviewsDbRepository);
//    }
//
//    private boolean isExistRequiredDataForGroupTraining(GroupTrainingRequest groupTrainingModel) {
//        String trainingName = groupTrainingModel.getTrainingName();
//        String trainerId = groupTrainingModel.getTrainerId();
//        String date = groupTrainingModel.getDate();
//        String startTime = groupTrainingModel.getStartTime();
//        String endTime = groupTrainingModel.getEndTime();
//
//        if (trainingName.isEmpty() || trainerId.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty())
//            return false;
//
//        return true;
//    }
//
//    private boolean isValidHallNo(int hallNo) {
//        if (hallNo <= 0)
//            return false;
//        return true;
//    }
//
//    private boolean isValidLimit(int limit) {
//        if (limit <= 0)
//            return false;
//        return true;
//    }
//
//    private boolean isTrainingRetroDateAndTime(String date) throws ParseException {
//        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
//        Date requestDateParsed = sdfDate.parse(date);
//        Date now = new Date();
//        String todayDateFormatted = sdfDate.format(now);
//        Date todayDateParsed = sdfDate.parse(todayDateFormatted);
//
//        if (requestDateParsed.before(todayDateParsed)) return true;
//
//        return false;
//    }
//
//    private boolean isStartTimeAfterEndTime(String startTime, String endTime) {
//
//        LocalTime start = LocalTime.parse(startTime);
//        LocalTime stop = LocalTime.parse(endTime);
//        Duration duration = Duration.between(start, stop);
//
//        if (duration.toMinutes() <= 0) return true;
//
//        return false;
//    }
//
//    @Override
//    public List<GroupTrainings> getGroupTrainings() {
//        return groupTrainingsDbRepository.getGroupTrainings();
//    }
//
//    @Override
//    public GroupTrainings getGroupTrainingById(String trainingId) throws NotExistingGroupTrainingException {
//        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
//            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
//        return groupTrainingsDbRepository.getGroupTrainingById(trainingId);
//    }
//
//    @Override
//    public List<String> getTrainingParticipants(String trainingId) throws NotExistingGroupTrainingException {
//        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
//            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
//        return groupTrainingsDbRepository.getTrainingParticipants(trainingId);
//    }
//
//    @Override
//    public void enrollToGroupTraining(String trainingId, String clientId) throws TrainingEnrollmentException {
//        if (trainingId.length() != 24 || !groupTrainingsDbRepository.isAbilityToGroupTrainingEnrollment(trainingId))
//            throw new TrainingEnrollmentException("Cannot enroll to this training");
//        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
//            throw new TrainingEnrollmentException("Client is already enrolled to this training");
//        groupTrainingsDbRepository.enrollToGroupTraining(trainingId, clientId);
//    }
//
//    @Override
//    public void addToReserveList(String trainingId, String clientId)
//            throws NotExistingGroupTrainingException, TrainingEnrollmentException {
//        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
//            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
//        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId))
//            throw new TrainingEnrollmentException("Client is already enrolled to this training");
//        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
//            throw new TrainingEnrollmentException("Client already exists in reserve list");
//
//        groupTrainingsDbRepository.addToReserveList(trainingId, clientId);
//    }
//
//    @Override
//    public void removeGroupTrainingEnrollment(String trainingId, String clientId)
//            throws NotExistingGroupTrainingException, TrainingEnrollmentException {
//        if (!groupTrainingsDbRepository.isGroupTrainingExist(trainingId))
//            throw new NotExistingGroupTrainingException("Training with ID " + trainingId + " does not exist");
//        if (!groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)
//                && !groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId))
//            throw new TrainingEnrollmentException("Client is not enrolled to this training");
//        if (groupTrainingsDbRepository.isClientAlreadyEnrolledToGroupTraining(trainingId, clientId)) {
//            groupTrainingsDbRepository.removeFromParticipants(trainingId, clientId);
//        }
//        if (groupTrainingsDbRepository.isClientAlreadyExistInReserveList(trainingId, clientId)) {
//            groupTrainingsDbRepository.removeFromReserveList(trainingId, clientId);
//        }
//    }
//
//    @Override
//    public GroupTrainings createGroupTraining(GroupTrainingRequest groupTrainingModel)
//            throws TrainingCreationException, ParseException, InvalidHourException {
//        if (!isExistRequiredDataForGroupTraining(groupTrainingModel))
//            throw new TrainingCreationException("Cannot create new group training. Missing required data.");
//
//        String date = groupTrainingModel.getDate();
//        String startTime = groupTrainingModel.getStartTime();
//        String endTime = groupTrainingModel.getEndTime();
//        int hallNo = groupTrainingModel.getHallNo();
//        int limit = groupTrainingModel.getLimit();
//
//        if (isTrainingRetroDateAndTime(date))
//            throw new TrainingCreationException("Cannot create new group training. Training retro date.");
//        if (isStartTimeAfterEndTime(startTime, endTime))
//            throw new TrainingCreationException("Cannot create new group training. Start time after end time.");
//        if (!isValidHallNo(hallNo))
//            throw new TrainingCreationException("Cannot create new group training. Invalid hall no.");
//        if (!isValidLimit(limit))
//            throw new TrainingCreationException("Cannot create new group training. Invalid limit.");
//        if (!groupTrainingsDbRepository.isAbilityToCreateTraining(groupTrainingModel))
//            throw new TrainingCreationException("Cannot create new group training");
//
//        return groupTrainingsDbRepository.createTraining(groupTrainingModel);
//    }


}