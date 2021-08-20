package com.healthy.gym.account.utils;

import com.github.javafaker.Faker;
import com.healthy.gym.account.data.document.NotificationDocument;
import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.dto.BasicUserInfoDTO;
import com.healthy.gym.account.enums.GymRole;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDocumentUtil {

    private static final Faker faker = new Faker();
    private static final ModelMapper mapper = new ModelMapper();

    private TestDocumentUtil() {
        throw new IllegalStateException("Test utility class.");
    }

    public static UserDocument getTestUser(String userId) {
        String name = faker.name().firstName();
        String surname = faker.name().lastName();
        String email = faker.internet().emailAddress();
        var roles = List.of(GymRole.USER);
        return new UserDocument(name, surname, email, userId, roles);
    }

    public static UserDocument getTestUser() {
        String userId = UUID.randomUUID().toString();
        return getTestUser(userId);
    }

    public static BasicUserInfoDTO getTestUserDTO() {
        return mapper.map(getTestUser(), BasicUserInfoDTO.class);
    }

    public static UserDocument getTestTrainer() {
        return getTestTrainer(UUID.randomUUID().toString());
    }

    public static UserDocument getTestTrainer(String userId) {
        var user = getTestUser(userId);
        user.setGymRoles(List.of(GymRole.USER, GymRole.TRAINER));
        return user;
    }


    public static List<UserDocument> getTestListOfUserDocuments(int numberOfUserDocuments) {
        if (numberOfUserDocuments <= 0) {
            throw new IllegalArgumentException("Number of users must be greater than 0.");
        }
        List<UserDocument> list = new ArrayList<>(numberOfUserDocuments);
        for (int i = 0; i < numberOfUserDocuments; i++) {
            list.add(getTestUser());
        }
        return list;
    }

    public static String getTestRemarks() {
        return faker.lorem().characters(280);
    }


    public static NotificationDocument getTestNotificationDocument(String createdAt) {
        var notification = new NotificationDocument();
        notification.setNotificationId(UUID.randomUUID().toString());
        String title = faker.lorem().sentences(1).get(0);
        notification.setTitle(title);
        String content = faker.lorem().sentences(1).get(0);
        notification.setContent(content);
        notification.setCreatedBy(TestDocumentUtil.getTestUser());
        notification.setMarkAsRead(true);
        notification.setCreatedAt(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return notification;
    }

}
