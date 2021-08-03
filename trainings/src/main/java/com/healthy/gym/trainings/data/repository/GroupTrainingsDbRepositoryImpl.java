package com.healthy.gym.trainings.data.repository;

import com.healthy.gym.trainings.model.request.GroupTrainingRequestOld;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Repository
public class GroupTrainingsDbRepositoryImpl implements GroupTrainingsDbRepository {

    private static final String GROUP_TRAININGS_COLLECTION_NAME = "GroupTrainings";

    private final Environment environment;
    private final GroupTrainingsRepository groupTrainingsRepository;

    @Autowired
    public GroupTrainingsDbRepositoryImpl(
            Environment environment,
            GroupTrainingsRepository groupTrainingsRepository
    ) {
        this.environment = environment;
        this.groupTrainingsRepository = groupTrainingsRepository;
    }

    @Override
    public boolean isAbilityToGroupTrainingEnrollment(String trainingId) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        String todayDateFormatted = sdfDate.format(now);
        String timeNowFormatted = sdfTime.format(now);

        if (!groupTrainingsRepository.existsByTrainingId(trainingId)) return false;

        int participantsCount = groupTrainingsRepository
                .getFirstByTrainingId(trainingId)
                .getParticipants()
                .size();

        boolean isAbilityInTheFutureEvents = groupTrainingsRepository
                .existsByTrainingIdAndDateAfterAndLimitGreaterThan(
                        trainingId,
                        todayDateFormatted,
                        participantsCount
                );
        boolean isAbilityInTheTodayEvents = groupTrainingsRepository
                .existsByTrainingIdAndDateEqualsAndStartTimeAfterAndLimitGreaterThan(
                        trainingId,
                        todayDateFormatted,
                        timeNowFormatted,
                        participantsCount
                );

        return isAbilityInTheFutureEvents || isAbilityInTheTodayEvents;
    }

    @Override
    public boolean isAbilityToCreateTraining(GroupTrainingRequestOld groupTrainingModel) {
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
        Document endLtEnd = new Document("endTime", lteEnd);
        Document endGteEnd = new Document("endTime", gteEnd);

        Document startLtEnd = new Document("startTime", ltEnd);
        Document eqHallNo = new Document("hallNo", hallNo);

        Document startDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, eqHallNo));
        Document endDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, endGtBeginning, endLtEnd, eqHallNo));
        Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                eqDate, startLteBeginning, endGteEnd, eqHallNo));

        Document match = new Document("$match", new Document(
                "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
        List<Document> pipeline = Arrays.asList(match);

        MongoCollection collection = getGroupTrainingsCollection();
        return !collection.aggregate(pipeline).cursor().hasNext();
    }

    private MongoCollection getGroupTrainingsCollection() {
        MongoClient mongoClient = MongoClients.create(environment.getProperty("spring.data.mongodb.uri"));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(environment.getProperty("spring.data.mongodb.database"));
        return mongoDatabase.getCollection(GROUP_TRAININGS_COLLECTION_NAME);
    }

    @Override
    public boolean isAbilityToUpdateTraining(String trainingId, GroupTrainingRequestOld groupTrainingModel) {
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

        Document startDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, startGteBeginning, startLtEnd, eqHallNo, notEqTrainingId));
        Document endDateDuringEvent = new Document("$and", Arrays.asList(
                eqDate, endGtBeginning, endLtEnd, eqHallNo, notEqTrainingId));
        Document longerThisTimeEvent = new Document("$and", Arrays.asList(
                eqDate, startLteBeginning, endGteEnd, eqHallNo, notEqTrainingId));

        Document match = new Document("$match", new Document(
                "$or", Arrays.asList(startDateDuringEvent, endDateDuringEvent, longerThisTimeEvent)));
        List<Document> pipeline = Arrays.asList(match);

        MongoCollection collection = getGroupTrainingsCollection();
        return !collection.aggregate(pipeline).cursor().hasNext();
    }
}
