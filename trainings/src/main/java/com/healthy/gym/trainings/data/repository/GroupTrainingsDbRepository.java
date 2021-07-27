package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.configuration.MongoConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.data.document.GroupTrainingsReviews;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingReviewResponse;
import com.healthy.gym.trainings.model.response.ParticipantsResponse;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class GroupTrainingsDbRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    private ReviewDAO groupTrainingsReviewsRepository;

    @Autowired
    private TrainingTypeDAO trainingTypeRepository;

    @Autowired
    private UserDAO userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoConfig mongoConfig;

    private MongoClient mongoClient;
    private MongoDatabase mdb;
    private static String groupTrainingsCollectionName = "GroupTrainings";
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private String defaultStartDate = "1900-01-01";
    private String defaultEndDate = "2099-12-31";
    private int page = 0;
    private int size = 1000000;
    private Pageable paging;
    private double initialRating = 0.0;

    public GroupTrainingsDbRepository(){
        paging = PageRequest.of(page, size);
    }


    public List<GroupTrainingResponse> getGroupTrainings(String startDate, String endDate) throws InvalidHourException, StartDateAfterEndDateException, ParseException, InvalidDateException {
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

        List<GroupTrainings> groupTrainingsDbResponse = groupTrainingsRepository.findByDateBetween(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted);

        List<GroupTrainingResponse> result = new ArrayList<>();
        for(GroupTrainings training : groupTrainingsDbResponse){

            List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                    null,
                    null,
                    training.getTrainingType().getTrainingTypeId(),
                    paging).getContent();

            double rating = 0.0;
            int sum = 0;
            int counter = 0;
            for(GroupTrainingReviewResponse review : groupTrainingsReviews){
                sum += review.getStars();
                counter++;
            }
            if(counter !=0 ) rating = sum/counter;

            List<UserDocument> participants = training.getParticipants();
            List<ParticipantsResponse> participantsResponses = new ArrayList<>();
            for(UserDocument userDocument : participants){
                ParticipantsResponse participantsResponse = new ParticipantsResponse(userDocument.getUserId(),
                        userDocument.getName(), userDocument.getSurname());
                participantsResponses.add(participantsResponse);
            }

            List<UserDocument> reserveList = training.getReserveList();
            List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
            for(UserDocument userDocument : reserveList){
                ParticipantsResponse reserveListResponse = new ParticipantsResponse(userDocument.getUserId(),
                        userDocument.getName(), userDocument.getSurname());
                reserveListResponses.add(reserveListResponse);
            }

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(training.getTrainingId(),
                    training.getTrainingType().getName(),
                    training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    rating,
                    participantsResponses,
                    reserveListResponses);
            result.add(groupTraining);
        }
        return result;
    }

    public List<GroupTrainingPublicResponse> getPublicGroupTrainings(String startDate, String endDate) throws InvalidHourException, InvalidDateException, StartDateAfterEndDateException, ParseException {
        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();

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

        List<GroupTrainings> groupTrainings = groupTrainingsRepository.findByDateBetween(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted);

        for(GroupTrainings groupTraining : groupTrainings){

            List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                    null,
                    null,
                    groupTraining.getTrainingType().getTrainingTypeId(),
                    paging).getContent();

            double rating = 0.0;
            int sum = 0;
            int counter = 0;
            for(GroupTrainingReviewResponse review : groupTrainingsReviews){
                sum += review.getStars();
                counter++;
            }
            if(counter !=0 ) rating = sum/counter;

            publicResponse.add(new GroupTrainingPublicResponse(groupTraining.getTrainingId(),
                    groupTraining.getTrainingType().getName(),
                    groupTraining.getTrainerId(),
                    groupTraining.getDate(),
                    groupTraining.getStartTime(),
                    groupTraining.getEndTime(),
                    groupTraining.getHallNo(),
                    groupTraining.getLimit(),
                    rating));
        }

        return publicResponse;
    }

    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws InvalidHourException, InvalidDateException {
        GroupTrainings groupTrainingsDbResponse = groupTrainingsRepository.findFirstByTrainingId(trainingId);

        List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                null,
                null,
                groupTrainingsDbResponse.getTrainingType().getTrainingTypeId(), paging).getContent();

        double rating = 0.0;
        int sum = 0;
        int counter = 0;
        for(GroupTrainingReviewResponse review : groupTrainingsReviews){
            sum += review.getStars();
            counter++;
        }
        if(counter !=0 ) rating = sum/counter;

        List<UserDocument> participants = groupTrainingsDbResponse.getParticipants();
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        for(UserDocument userDocument : participants){
            ParticipantsResponse participantsResponse = new ParticipantsResponse(userDocument.getUserId(),
                    userDocument.getName(), userDocument.getSurname());
            participantsResponses.add(participantsResponse);
        }

        List<UserDocument> reserveList = groupTrainingsDbResponse.getReserveList();
        List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
        for(UserDocument userDocument : reserveList){
            ParticipantsResponse reserveListResponse = new ParticipantsResponse(userDocument.getUserId(),
                    userDocument.getName(), userDocument.getSurname());
            reserveListResponses.add(reserveListResponse);
        }

        GroupTrainingResponse result = new GroupTrainingResponse(groupTrainingsDbResponse.getTrainingId(),
                groupTrainingsDbResponse.getTrainingType().getName(),
                groupTrainingsDbResponse.getTrainerId(),
                groupTrainingsDbResponse.getDate(),
                groupTrainingsDbResponse.getStartTime(),
                groupTrainingsDbResponse.getEndTime(),
                groupTrainingsDbResponse.getHallNo(),
                groupTrainingsDbResponse.getLimit(),
                rating,
                participantsResponses,
                reserveListResponses);
        return result;
    }

    public List<GroupTrainingResponse> getGroupTrainingsByTrainingTypeId(String trainingTypeId, String startDate, String endDate) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException {
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

        List<GroupTrainings> groupTrainingsDbResponse = groupTrainingsRepository.findAllByTrainingTypeIdAndDateBetween(
                trainingTypeId, startDateMinusOneDayFormatted, endDatePlusOneDayFormatted);

        double rating = 0.0;
        int sum = 0;
        int counter = 0;
        if(groupTrainingsDbResponse.size() > 0){
            List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                    null,
                    null,
                    groupTrainingsDbResponse.get(0).getTrainingType().getTrainingTypeId(), paging).getContent();

            for(GroupTrainingReviewResponse review : groupTrainingsReviews){
                sum += review.getStars();
                counter++;
            }
            if(counter !=0 ) rating = sum/counter;
        }

        List<GroupTrainingResponse> result = new ArrayList<>();
        for(GroupTrainings training : groupTrainingsDbResponse){

            List<ParticipantsResponse> participantsResponses = new ArrayList<>();
            List<UserDocument> participants = training.getParticipants();
            for(UserDocument userDocument : participants){
                ParticipantsResponse participantsResponse = new ParticipantsResponse(userDocument.getUserId(),
                        userDocument.getName(), userDocument.getSurname());
                participantsResponses.add(participantsResponse);
            }

            List<UserDocument> reserveList = training.getReserveList();
            List<ParticipantsResponse> reserveListResponses = new ArrayList<>();
            for(UserDocument userDocument : reserveList){
                ParticipantsResponse reserveListResponse = new ParticipantsResponse(userDocument.getUserId(),
                        userDocument.getName(), userDocument.getSurname());
                reserveListResponses.add(reserveListResponse);
            }

            GroupTrainingResponse groupTraining = new GroupTrainingResponse(training.getTrainingId(),
                    training.getTrainingType().getName(),
                    training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    rating,
                    participantsResponses,
                    reserveListResponses);
            result.add(groupTraining);
        }
        return result;
    }

    public List<GroupTrainingPublicResponse> getGroupTrainingsPublicByTrainingTypeId(String trainingTypeId, String startDate, String endDate) throws ParseException, StartDateAfterEndDateException, InvalidDateException, InvalidHourException {
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

        List<GroupTrainings> dbResponse = groupTrainingsRepository.findAllByTrainingTypeIdAndDateBetween(
                trainingTypeId, startDateMinusOneDayFormatted, endDatePlusOneDayFormatted);

        double rating = 0.0;
        int sum = 0;
        int counter = 0;
        if(dbResponse.size() > 0){
            List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                    null,
                    null,
                    dbResponse.get(0).getTrainingType().getTrainingTypeId(), paging).getContent();

            for(GroupTrainingReviewResponse review : groupTrainingsReviews){
                sum += review.getStars();
                counter++;
            }
            if(counter !=0 ) rating = sum/counter;
        }

        List<GroupTrainingPublicResponse> result = new ArrayList<>();
        for(GroupTrainings training : dbResponse){
            GroupTrainingPublicResponse groupTraining = new GroupTrainingPublicResponse(training.getTrainingId(),
                    training.getTrainingType().getName(),
                    training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    rating);
            result.add(groupTraining);
        }
        return result;
    }

    public List<GroupTrainingPublicResponse> getMyAllGroupTrainings(String clientId) throws InvalidDateException, InvalidHourException {
        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        List<GroupTrainings> groupTrainings = groupTrainingsRepository.findGroupTrainingsByParticipantsContains(clientId);

        for(GroupTrainings groupTraining : groupTrainings){

            double rating = 0.0;
            int sum = 0;
            int counter = 0;
            if(groupTrainings.size() > 0){
                List<GroupTrainingReviewResponse> groupTrainingsReviews = groupTrainingsReviewsRepository.findByDateBetweenAndTrainingTypeId(
                        null,
                        null,
                        groupTraining.getTrainingType().getTrainingTypeId(), paging).getContent();

                for(GroupTrainingReviewResponse review : groupTrainingsReviews){
                    sum += review.getStars();
                    counter++;
                }
                if(counter !=0 ) rating = sum/counter;
            }

            publicResponse.add(new GroupTrainingPublicResponse(groupTraining.getTrainingId(),
                    groupTraining.getTrainingType().getName(),
                    groupTraining.getTrainerId(),
                    groupTraining.getDate(),
                    groupTraining.getStartTime(),
                    groupTraining.getEndTime(),
                    groupTraining.getHallNo(),
                    groupTraining.getLimit(),
                    rating));
        }

        return publicResponse;
    }

    public List<ParticipantsResponse> getTrainingParticipants(String trainingId){

        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        List<UserDocument> participants = groupTrainingsRepository.getFirstByTrainingId(trainingId).getParticipants();
        for(UserDocument userDocument : participants){
            ParticipantsResponse participantsResponse = new ParticipantsResponse(userDocument.getUserId(),
                    userDocument.getName(), userDocument.getSurname());
            participantsResponses.add(participantsResponse);
        }

        return participantsResponses;
    }

    public boolean isGroupTrainingExist(String trainingId){
        return groupTrainingsRepository.existsByTrainingId(trainingId);
    }

    public boolean isGroupTrainingExistByType(String trainingTypeId){
        return groupTrainingsRepository.existsByTrainingTypeId(trainingTypeId);
    }

    public boolean isAbilityToGroupTrainingEnrollment(String trainingId){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        String timeNowFormatted = sdfTime.format(now);

        if(!groupTrainingsRepository.existsByTrainingId(trainingId)) return false;

        int participantsCount = groupTrainingsRepository.getFirstByTrainingId(trainingId).getParticipants().size();

        boolean isAbilityInTheFutureEvents = groupTrainingsRepository.existsByTrainingIdAndDateAfterAndLimitGreaterThan(trainingId,
                todayDateFormatted, participantsCount);
        boolean isAbilityInTheTodayEvents = groupTrainingsRepository.existsByTrainingIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(
                trainingId, todayDateFormatted, timeNowFormatted, participantsCount);

        return isAbilityInTheFutureEvents || isAbilityInTheTodayEvents;
    }

    public boolean isClientAlreadyEnrolledToGroupTraining(String trainingId, String clientId){
        List<UserDocument> participantsUsers = groupTrainingsRepository.getFirstByTrainingId(trainingId).getParticipants();
        List<String> usersIds = new ArrayList<>();
        for(UserDocument userDocument : participantsUsers){
            usersIds.add(userDocument.getUserId());
        }
        return usersIds.contains(clientId);
    }

    public boolean isClientAlreadyExistInReserveList(String trainingId, String clientId){
        List<UserDocument> reserveListUsers = groupTrainingsRepository.getFirstByTrainingId(trainingId).getReserveList();
        List<String> usersIds = new ArrayList<>();
        for(UserDocument userDocument : reserveListUsers){
            usersIds.add(userDocument.getUserId());
        }
        return usersIds.contains(clientId);
    }

    public void enrollToGroupTraining(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument newParticipant = userRepository.findByUserId(clientId);
        List<UserDocument> participants = groupTrainings.getParticipants();
        participants.add(newParticipant);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void addToReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument newReserveListParticipant = userRepository.findByUserId(clientId);
        List<UserDocument> reserveList = groupTrainings.getReserveList();
        reserveList.add(newReserveListParticipant);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromParticipants(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument participantToRemove = userRepository.findByUserId(clientId);
        List<UserDocument> participants = groupTrainings.getParticipants();
        participants.remove(participantToRemove);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        UserDocument reserveListParticipantToRemove = userRepository.findByUserId(clientId);
        List<UserDocument> reserveList = groupTrainings.getReserveList();
        reserveList.remove(reserveListParticipantToRemove);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    public boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel) throws ParseException {

            MongoClient mongoClient = MongoClients.create(environment.getProperty("spring.data.mongodb.uri"));
            mdb = mongoClient.getDatabase(environment.getProperty("spring.data.mongodb.database"));
            MongoCollection collection = mdb.getCollection(groupTrainingsCollectionName);

            String date = groupTrainingModel.getDate();
            String startTime = groupTrainingModel.getStartTime();
            String endTime = groupTrainingModel.getEndTime();
            int hallNo = groupTrainingModel.getHallNo();

            Document eqDate = new Document("date", date);

            Document gtBeginning = new Document("$gt", startTime);
            Document gteBeginning = new Document("$gte", startTime);
            Document lteBeginning = new Document("$lte", startTime);
            Document startGteBeginning = new Document("startTime", gteBeginning);
            Document endGtBeginning = new Document("endTime", gtBeginning);
            Document startLteBeginning = new Document("startTime", lteBeginning);
            Document ltEnd = new Document("$lt", endTime);
            Document lteEnd = new Document("$lte", endTime);
            Document gteEnd = new Document("$gte", endTime);
            Document startLtEnd = new Document("startTime", ltEnd);
            Document endLtEnd = new Document("endTime", lteEnd);
            Document endGteEnd = new Document("endTime", gteEnd);

            Document eqHallNo = new Document("hallNo", hallNo);

            Document middleTimeEventValid = new Document("$and", Arrays.asList(
                    eqDate, startGteBeginning, startLtEnd, endGtBeginning, endLtEnd, eqHallNo));
            Document startDateDuringEvent = new Document("$and", Arrays.asList(
                    eqDate, startGteBeginning, startLtEnd, eqHallNo));
            Document endDateDuringEvent = new Document("$and", Arrays.asList(
                    eqDate, endGtBeginning, endLtEnd, eqHallNo));
            Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                    eqDate, startLteBeginning, endGteEnd, eqHallNo));

            Document match = new Document("$match", new Document(
                    "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
            List<Document> pipeline = Arrays.asList(match);

            return !collection.aggregate(pipeline).cursor().hasNext();
    }

    public boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel) throws ParseException {

            MongoClient mongoClient = MongoClients.create(environment.getProperty("spring.data.mongodb.uri"));
            mdb = mongoClient.getDatabase(environment.getProperty("spring.data.mongodb.database"));
            MongoCollection collection = mdb.getCollection(groupTrainingsCollectionName);

            String date = groupTrainingModel.getDate();
            String startTime = groupTrainingModel.getStartTime();
            String endTime = groupTrainingModel.getEndTime();
            int hallNo = groupTrainingModel.getHallNo();

            Document eqDate = new Document("date", date);

            Document gtBeginning = new Document("$gt", startTime);
            Document gteBeginning = new Document("$gte", startTime);
            Document lteBeginning = new Document("$lte", startTime);
            Document startGteBeginning = new Document("startTime", gteBeginning);
            Document endGtBeginning = new Document("endTime", gtBeginning);
            Document startLteBeginning = new Document("startTime", lteBeginning);
            Document ltEnd = new Document("$lt", endTime);
            Document lteEnd = new Document("$lte", endTime);
            Document gteEnd = new Document("$gte", endTime);
            Document startLtEnd = new Document("startTime", ltEnd);
            Document endLtEnd = new Document("endTime", lteEnd);
            Document endGteEnd = new Document("endTime", gteEnd);

            Document eqHallNo = new Document("hallNo", hallNo);
            Document notEqTrainingId = new Document("ne", new Document("trainingId", trainingId));

            Document middleTimeEventValid = new Document("$and", Arrays.asList(
                    eqDate, startGteBeginning, startLtEnd, endGtBeginning, endLtEnd, eqHallNo, notEqTrainingId));
            Document startDateDuringEvent = new Document("$and", Arrays.asList(
                    eqDate, startGteBeginning, startLtEnd, eqHallNo, notEqTrainingId));
            Document endDateDuringEvent = new Document("$and", Arrays.asList(
                    eqDate, endGtBeginning, endLtEnd, eqHallNo, notEqTrainingId));
            Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                    eqDate, startLteBeginning, endGteEnd, eqHallNo, notEqTrainingId));

            Document match = new Document("$match", new Document(
                    "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
            List<Document> pipeline = Arrays.asList(match);

            return !collection.aggregate(pipeline).cursor().hasNext();
    }

    public GroupTrainings createTraining(GroupTrainingRequest groupTrainingModel) throws InvalidHourException {
        String trainingId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType = trainingTypeRepository.findByTrainingTypeId(
                groupTrainingModel.getTrainingTypeId());

        List<String> participantsIds = groupTrainingModel.getParticipants();
        List<UserDocument> participants = new ArrayList<>();
        for(String participantId : participantsIds){
            UserDocument participant = userRepository.findByUserId(participantId);
            participants.add(participant);
        }

        List<String> reserveListParticipantsIds = groupTrainingModel.getReserveList();
        List<UserDocument> reserveList = new ArrayList<>();
        for(String reserveListParticipantId : reserveListParticipantsIds){
            UserDocument reserveListParticipant = userRepository.findByUserId(reserveListParticipantId);
            reserveList.add(reserveListParticipant);
        }

        GroupTrainings response = groupTrainingsRepository.insert(new GroupTrainings(trainingId,
                trainingType,
                groupTrainingModel.getTrainerId(),
                groupTrainingModel.getDate(),
                groupTrainingModel.getStartTime(),
                groupTrainingModel.getEndTime(),
                groupTrainingModel.getHallNo(),
                groupTrainingModel.getLimit(),
                participants,
                reserveList
        ));
        return response;
    }

    public GroupTrainings removeTraining(String trainingId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        groupTrainingsRepository.removeByTrainingId(trainingId);
        return groupTrainings;
    }

    public GroupTrainings updateTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest) throws InvalidHourException {
        boolean ifExistGroupTraining = groupTrainingsRepository.existsByTrainingId(trainingId);

        GroupTrainings groupTrainings = null;
        if(ifExistGroupTraining){
            groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
            TrainingTypeDocument trainingType = trainingTypeRepository.findByTrainingTypeId(
                    groupTrainingModelRequest.getTrainingTypeId());
            groupTrainings.setTrainingType(trainingType);
            groupTrainings.setTrainerId(groupTrainingModelRequest.getTrainerId());
            groupTrainings.setDate(groupTrainingModelRequest.getDate());
            groupTrainings.setStartTime(groupTrainingModelRequest.getStartTime());
            groupTrainings.setEndTime(groupTrainingModelRequest.getEndTime());
            groupTrainings.setHallNo(groupTrainingModelRequest.getHallNo());
            groupTrainings.setLimit(groupTrainingModelRequest.getLimit());
            GroupTrainings response = groupTrainingsRepository.save(groupTrainings);
        }

        return groupTrainings;
    }
}
