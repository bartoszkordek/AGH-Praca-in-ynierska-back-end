package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.configuration.MongoConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
import com.healthy.gym.trainings.model.response.GroupTrainingResponse;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoConfig mongoConfig;

    private MongoClient mongoClient;
    private MongoDatabase mdb;
    private static String groupTrainingsCollectionName = "GroupTrainings";
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    private String defaultStartDate = "1900-01-01";
    private String defaultEndDate = "2099-12-31";


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

        List<GroupTrainings> dbResponse = groupTrainingsRepository.findByDateBetween(startDateMinusOneDayFormatted,
                endDatePlusOneDayFormatted);
        List<GroupTrainingResponse> result = new ArrayList<>();
        for(GroupTrainings training : dbResponse){
            GroupTrainingResponse groupTraining = new GroupTrainingResponse(training.getTrainingId(),
                    training.getTrainingTypeId(),
                    training.getTrainerId(),
                    training.getDate(),
                    training.getStartTime(),
                    training.getEndTime(),
                    training.getHallNo(),
                    training.getLimit(),
                    training.getParticipants(),
                    training.getReserveList());
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
            publicResponse.add(new GroupTrainingPublicResponse(groupTraining.getTrainingId(),
                    groupTraining.getTrainingTypeId(),
                    groupTraining.getTrainerId(),
                    groupTraining.getDate(),
                    groupTraining.getStartTime(),
                    groupTraining.getEndTime(),
                    groupTraining.getHallNo(),
                    groupTraining.getLimit()));
        }

        return publicResponse;
    }

    public GroupTrainingResponse getGroupTrainingById(String trainingId) throws InvalidHourException, InvalidDateException {
        GroupTrainings dbResponse = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        GroupTrainingResponse result = new GroupTrainingResponse(dbResponse.getTrainingId(),
                dbResponse.getTrainingTypeId(),
                dbResponse.getTrainerId(),
                dbResponse.getDate(),
                dbResponse.getStartTime(),
                dbResponse.getEndTime(),
                dbResponse.getHallNo(),
                dbResponse.getLimit(),
                dbResponse.getParticipants(),
                dbResponse.getReserveList());
        return result;
    }

    public List<GroupTrainingPublicResponse> getMyAllGroupTrainings(String clientId) throws InvalidDateException, InvalidHourException {
        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        List<GroupTrainings> groupTrainings = groupTrainingsRepository.findGroupTrainingsByParticipantsContains(clientId);
        for(GroupTrainings groupTraining : groupTrainings){
            publicResponse.add(new GroupTrainingPublicResponse(groupTraining.getTrainingId(),
                    groupTraining.getTrainingTypeId(),
                    groupTraining.getTrainerId(),
                    groupTraining.getDate(),
                    groupTraining.getStartTime(),
                    groupTraining.getEndTime(),
                    groupTraining.getHallNo(),
                    groupTraining.getLimit()));
        }

        return publicResponse;
    }

    public List<String> getTrainingParticipants(String trainingId){
        return groupTrainingsRepository.getFirstByTrainingId(trainingId).getParticipants();
    }

    public boolean isGroupTrainingExist(String trainingId){
        return groupTrainingsRepository.existsByTrainingId(trainingId);
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
        return groupTrainingsRepository.getFirstByTrainingId(trainingId).getParticipants().contains(clientId);
    }

    public boolean isClientAlreadyExistInReserveList(String trainingId, String clientId){
        return groupTrainingsRepository.getFirstByTrainingId(trainingId).getReserveList().contains(clientId);
    }

    public void enrollToGroupTraining(String trainingId, String participantId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        List<String> participants = groupTrainings.getParticipants();
        participants.add(participantId);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void addToReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        List<String> reserveList = groupTrainings.getReserveList();
        reserveList.add(clientId);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromParticipants(String trainingId, String participantId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        List<String> participants = groupTrainings.getParticipants();
        participants.remove(participantId);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstByTrainingId(trainingId);
        List<String> reserveList = groupTrainings.getReserveList();
        reserveList.remove(clientId);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    public boolean isAbilityToCreateTraining(GroupTrainingRequest groupTrainingModel) throws ParseException {

        try {
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
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return false;
    }

    public boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequest groupTrainingModel) throws ParseException {

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
        return false;
    }

    public GroupTrainings createTraining(GroupTrainingRequest groupTrainingModel) throws InvalidHourException {
        String trainingId = UUID.randomUUID().toString();
        GroupTrainings response = groupTrainingsRepository.insert(new GroupTrainings(trainingId,
                groupTrainingModel.getTrainingTypeId(),
                groupTrainingModel.getTrainerId(),
                groupTrainingModel.getDate(),
                groupTrainingModel.getStartTime(),
                groupTrainingModel.getEndTime(),
                groupTrainingModel.getHallNo(),
                groupTrainingModel.getLimit(),
                groupTrainingModel.getParticipants(),
                groupTrainingModel.getReserveList()
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
            groupTrainings.setTrainingTypeId(groupTrainingModelRequest.getTrainingTypeId());
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
