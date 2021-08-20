package com.healthy.gym.account.utils;

import com.healthy.gym.account.data.document.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.healthy.gym.account.utils.TestDocumentUtil.getTestTrainer;
import static com.healthy.gym.account.utils.TestDocumentUtil.getTestUser;

@Component
public class TestDocumentUtilComponent {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public TestDocumentUtilComponent(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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

    public UserDocument saveAndGetTestTrainer(String userId) {
        return mongoTemplate.save(getTestTrainer(userId));
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
}
