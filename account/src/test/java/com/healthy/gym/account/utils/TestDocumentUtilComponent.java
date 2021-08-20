package com.healthy.gym.account.utils;

import com.github.javafaker.Faker;
import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.healthy.gym.account.utils.TestDocumentUtil.*;

@Component
public class TestDocumentUtilComponent {

    private final MongoTemplate mongoTemplate;
    private final Faker faker;

    @Autowired
    public TestDocumentUtilComponent(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.faker = new Faker();
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

    public NotificationDocument saveAndGetNotification(String createdAt, boolean markAsRead) {
        var user = saveAndGetTestUser();
        var notification = getTestNotificationDocument(createdAt, markAsRead, user);
        return mongoTemplate.save(notification);
    }

    public NotificationDocument saveAndGetNotification(
            String createdAt,
            boolean markAsRead,
            UserDocument savedUserDocument
    ) {
        var notification = getTestNotificationDocument(createdAt, markAsRead, savedUserDocument);
        return mongoTemplate.save(notification);
    }

    public NotificationDocument saveAndGetTestNotificationDocument(
            boolean markAsRead,
            UserDocument toUserDocument
    ) {
        String userId = UUID.randomUUID().toString();
        saveAndGetTestUser(userId);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(token);

        var notification = getNotificationDocument(
                UUID.randomUUID().toString(),
                faker.lorem().sentences(1).get(0),
                faker.lorem().sentences(1).get(0),
                toUserDocument,
                markAsRead
        );

        return mongoTemplate.save(notification);
    }

    public NotificationDocument saveAndGetTestNotificationDocument() {
        String userId = UUID.randomUUID().toString();
        saveAndGetTestUser(userId);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userId, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(token);

        var notification = getNotificationDocument(
                UUID.randomUUID().toString(),
                faker.lorem().sentences(1).get(0),
                faker.lorem().sentences(1).get(0),
                saveAndGetTestUser(),
                faker.random().nextBoolean()
        );

        return mongoTemplate.save(notification);
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
