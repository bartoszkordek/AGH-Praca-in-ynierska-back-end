package com.healthy.gym.auth.service;

import com.healthy.gym.auth.component.Translator;
import com.healthy.gym.auth.data.document.NotificationDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.NotificationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO;
    private final Translator translator;

    @Autowired
    public NotificationServiceImpl(NotificationDAO notificationDAO, Translator translator) {
        this.notificationDAO = notificationDAO;
        this.translator = translator;
    }

    @Override
    public NotificationDocument createWelcomeNotification(UserDocument userDocument) {
        String title = translator.toLocale("notification.welcome.title");
        String content = translator.toLocale("notification.welcome.content");

        NotificationDocument notificationDocument = new NotificationDocument(
                UUID.randomUUID().toString(),
                userDocument,
                title,
                content
        );

        return notificationDAO.save(notificationDocument);
    }
}
