package com.healthy.gym.trainings.test.utils;

import com.healthy.gym.trainings.data.document.GroupTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

        String groupTrainingId = UUID.randomUUID().toString();
        TrainingTypeDocument trainingType = saveAndGetTestTrainingType();
        UserDocument trainer = saveAndGetTestTrainer();
        LocationDocument location = saveAndGetTestLocation();
        UserDocument user1 = saveAndGetTestUser();
        UserDocument user2 = saveAndGetTestUser();

        var basicList = isInBasic ? List.of(user1, user2, savedUserDocument) : List.of(user2);
        var reserveList = isInReserve ? List.of(user1, user2, savedUserDocument) : List.of(user2);

        GroupTrainingDocument groupTrainingDocument = new GroupTrainingDocument(
                groupTrainingId,
                trainingType,
                List.of(trainer),
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                location,
                20,
                basicList,
                reserveList
        );
        return mongoTemplate.save(groupTrainingDocument);
    }
}
