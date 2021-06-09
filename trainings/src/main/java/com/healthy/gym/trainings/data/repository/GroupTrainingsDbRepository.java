package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.config.MongoConfig;
import com.healthy.gym.trainings.data.document.GroupTrainings;
import com.healthy.gym.trainings.exception.InvalidDateException;
import com.healthy.gym.trainings.exception.InvalidHourException;
import com.healthy.gym.trainings.model.request.GroupTrainingRequest;
import com.healthy.gym.trainings.model.response.GroupTrainingPublicResponse;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    public List<GroupTrainings> getGroupTrainings(){
        return groupTrainingsRepository.findAll();
    }

    public List<GroupTrainingPublicResponse> getPublicGroupTrainings() throws InvalidHourException, InvalidDateException {
        List<GroupTrainingPublicResponse> publicResponse = new ArrayList<>();
        List<GroupTrainings> groupTrainings = groupTrainingsRepository.findAll();
        for(GroupTrainings groupTraining : groupTrainings){
            publicResponse.add(new GroupTrainingPublicResponse(groupTraining.getTrainingName(),
                    groupTraining.getTrainerId(),
                    groupTraining.getDate(),
                    groupTraining.getStartTime(),
                    groupTraining.getEndTime(),
                    groupTraining.getHallNo(),
                    groupTraining.getLimit()));
        }

        return publicResponse;
    }

    public GroupTrainings getGroupTrainingById(String trainingId){
        return groupTrainingsRepository.findFirstById(trainingId);
    }

    public List<GroupTrainings> getMyAllGroupTrainings(String clientId){
        return groupTrainingsRepository.findGroupTrainingsByParticipantsContains(clientId);
    }

    public List<String> getTrainingParticipants(String trainingId){
        return groupTrainingsRepository.getFirstById(trainingId).getParticipants();
    }

    public boolean isGroupTrainingExist(String trainingId){
        return groupTrainingsRepository.existsById(trainingId);
    }

    public boolean isAbilityToGroupTrainingEnrollment(String trainingId){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        String timeNowFormatted = sdfTime.format(now);

        if(!groupTrainingsRepository.existsById(trainingId)) return false;

        int participantsCount = groupTrainingsRepository.getFirstById(trainingId).getParticipants().size();

        boolean isAbilityInTheFutureEvents = groupTrainingsRepository.existsByIdAndDateAfterAndLimitGreaterThan(trainingId,
                todayDateFormatted, participantsCount);
        boolean isAbilityInTheTodayEvents = groupTrainingsRepository.existsByIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(
                trainingId, todayDateFormatted, timeNowFormatted, participantsCount);

        return isAbilityInTheFutureEvents || isAbilityInTheTodayEvents;
    }

    public boolean isClientAlreadyEnrolledToGroupTraining(String trainingId, String clientId){
        return groupTrainingsRepository.getFirstById(trainingId).getParticipants().contains(clientId);
    }

    public boolean isClientAlreadyExistInReserveList(String trainingId, String clientId){
        return groupTrainingsRepository.getFirstById(trainingId).getReserveList().contains(clientId);
    }

    public void enrollToGroupTraining(String trainingId, String participantId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        List<String> participants = groupTrainings.getParticipants();
        participants.add(participantId);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void addToReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        List<String> reserveList = groupTrainings.getReserveList();
        reserveList.add(clientId);
        groupTrainings.setReserveList(reserveList);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromParticipants(String trainingId, String participantId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        List<String> participants = groupTrainings.getParticipants();
        participants.remove(participantId);
        groupTrainings.setParticipants(participants);
        groupTrainingsRepository.save(groupTrainings);
    }

    public void removeFromReserveList(String trainingId, String clientId){
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        List<String> reserveList = groupTrainings.getReserveList();
        reserveList.remove(clientId);
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
        Document startLteBeginning = new Document("startTime",lteBeginning);
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

    public GroupTrainings createTraining(GroupTrainingRequest groupTrainingModel) throws InvalidHourException {
        GroupTrainings response = groupTrainingsRepository.insert(new GroupTrainings(
                groupTrainingModel.getTrainingName(),
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
        GroupTrainings groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
        groupTrainingsRepository.removeById(trainingId);
        return groupTrainings;
    }

    public GroupTrainings updateTraining(String trainingId, GroupTrainingRequest groupTrainingModelRequest) throws InvalidHourException {
        boolean ifExistGroupTraining = groupTrainingsRepository.existsById(trainingId);

        GroupTrainings groupTrainings = null;
        if(ifExistGroupTraining){
            groupTrainings = groupTrainingsRepository.findFirstById(trainingId);
            groupTrainings.setTrainingName(groupTrainingModelRequest.getTrainingName());
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
