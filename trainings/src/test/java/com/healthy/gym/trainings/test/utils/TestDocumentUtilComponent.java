package com.healthy.gym.trainings.test.utils;

import com.healthy.gym.trainings.data.document.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.trainings.test.utils.TestDocumentUtil.*;

@Component
public class TestDocumentUtilComponent {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TestDocumentUtilComponent(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public TrainingTypeDocument saveAndGetTestTrainingType() {
        return mongoTemplate.save(getTestTrainingType());
    }

    public UserDocument saveAndGetTestUser() {
        return mongoTemplate.save(getTestUser());
    }

    public UserDocument saveAndGetTestUser(String userId) {
        return mongoTemplate.save(getTestUser(userId));
    }

    public UserDocument saveAndGetTestTrainer() {
        return mongoTemplate.save(getTestTrainer());
    }

    public LocationDocument saveAndGetTestLocation() {
        return mongoTemplate.save(getTestLocation());
    }

    public GroupTrainingDocument createTestGroupTraining(
            UserDocument savedUserDocument,
            String startDate,
            String endDate,
            boolean isInBasic,
            boolean isInReserve
    ) {
        var basicList = getTestListOfSavedUserDocuments(5);
        if (isInBasic) basicList.add(savedUserDocument);

        var reserveList = getTestListOfSavedUserDocuments(2);
        if (isInReserve) reserveList.add(savedUserDocument);

        return saveAndGetTestGroupTraining(
                saveAndGetTestTrainingType(),
                List.of(saveAndGetTestTrainer()),
                saveAndGetTestLocation(),
                startDate,
                endDate,
                20,
                basicList,
                reserveList
        );
    }

    public List<UserDocument> getTestListOfSavedUserDocuments(int numberOfUserDocuments) {
        if (numberOfUserDocuments <= 0) {
            throw new IllegalArgumentException("Number of users must be greater than 0.");
        }
        List<UserDocument> list = new ArrayList<>(numberOfUserDocuments);
        for (int i = 0; i < numberOfUserDocuments; i++) {
            list.add(saveAndGetTestUser());
        }
        return list;
    }

    public List<UserDocument> getTestListOfSavedTrainersDocuments(int numberOfUserDocuments) {
        if (numberOfUserDocuments <= 0) {
            throw new IllegalArgumentException("Number of users must be greater than 0.");
        }
        List<UserDocument> list = new ArrayList<>(numberOfUserDocuments);
        for (int i = 0; i < numberOfUserDocuments; i++) {
            list.add(saveAndGetTestTrainer());
        }
        return list;
    }

    public GroupTrainingDocument createTestGroupTraining(String startDate, String endDate) {
        return saveAndGetTestGroupTraining(
                saveAndGetTestTrainingType(),
                List.of(saveAndGetTestTrainer()),
                saveAndGetTestLocation(),
                startDate,
                endDate,
                10,
                getTestListOfSavedUserDocuments(10),
                getTestListOfSavedUserDocuments(2)
        );
    }

    public GroupTrainingDocument createTestGroupTraining(
            TrainingTypeDocument savedTrainingType,
            String startDate,
            String endDate
    ) {
        return saveAndGetTestGroupTraining(
                savedTrainingType,
                List.of(saveAndGetTestTrainer()),
                saveAndGetTestLocation(),
                startDate,
                endDate,
                10,
                getTestListOfSavedUserDocuments(10),
                getTestListOfSavedUserDocuments(2)
        );
    }

    private GroupTrainingDocument saveAndGetTestGroupTraining(
            TrainingTypeDocument savedTrainingTypeDocument,
            List<UserDocument> savedTrainersList,
            LocationDocument savedLocationDocument,
            String startDate,
            String endDate,
            int limit,
            List<UserDocument> basicList,
            List<UserDocument> reserveList
    ) {
        String groupTrainingId = UUID.randomUUID().toString();

        GroupTrainingDocument groupTrainingDocument = new GroupTrainingDocument(
                groupTrainingId,
                savedTrainingTypeDocument,
                savedTrainersList,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                savedLocationDocument,
                limit,
                basicList,
                reserveList
        );
        return mongoTemplate.save(groupTrainingDocument);
    }

    public IndividualTrainingDocument saveAndGetTestIndividualTraining(
            TrainingTypeDocument savedTrainingTypeDocument,
            List<UserDocument> savedBasicList,
            List<UserDocument> savedTrainersList,
            String startDate,
            String endDate,
            LocationDocument savedLocationDocument,
            String remarks,
            boolean isAccepted
    ) {
        IndividualTrainingDocument trainingDocument = getTestIndividualTraining(
                savedTrainingTypeDocument,
                savedBasicList,
                savedTrainersList,
                startDate,
                endDate,
                savedLocationDocument,
                remarks
        );
        trainingDocument.setAccepted(isAccepted);
        return mongoTemplate.save(trainingDocument);
    }

    public IndividualTrainingDocument saveAndGetTestIndividualTraining(
            String startDateTime,
            String endDateTime,
            boolean isAccepted
    ) {
        return saveAndGetTestIndividualTraining(
                saveAndGetTestTrainingType(),
                getTestListOfSavedUserDocuments(5),
                getTestListOfSavedTrainersDocuments(1),
                startDateTime,
                endDateTime,
                saveAndGetTestLocation(),
                getTestRemarks(),
                isAccepted
        );
    }

    public IndividualTrainingDocument saveAndGetTestIndividualTraining(
            String startDateTime,
            String endDateTime
    ) {
        return saveAndGetTestIndividualTraining(
                saveAndGetTestTrainingType(),
                getTestListOfSavedUserDocuments(5),
                getTestListOfSavedTrainersDocuments(1),
                startDateTime,
                endDateTime,
                saveAndGetTestLocation(),
                getTestRemarks(),
                false
        );
    }
}
