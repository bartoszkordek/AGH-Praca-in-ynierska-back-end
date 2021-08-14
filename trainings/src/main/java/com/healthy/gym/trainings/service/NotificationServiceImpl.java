package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.NotificationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.NotificationDAO;
import com.healthy.gym.trainings.events.OnGroupTrainingUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDAO notificationDAO;
    private final Translator translator;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public NotificationServiceImpl(
            NotificationDAO notificationDAO,
            Translator translator,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.notificationDAO = notificationDAO;
        this.translator = translator;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void sendNotificationsAndEmailsWhenUpdatingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> users,
            boolean shouldSendEmails
    ) {
        String title = trainingName + " " + getFormattedDate(startDateTime);
        String content = translator.toLocale("notification.group.training.update");
        var notifications = users
                .stream()
                .map(user -> new NotificationDocument(user, title, content))
                .collect(Collectors.toList());
        notificationDAO.saveAll(notifications);

        if (!shouldSendEmails) return;

        List<String> emails = users
                .stream()
                .map(UserDocument::getEmail)
                .collect(Collectors.toList());

        applicationEventPublisher.publishEvent(
                new OnGroupTrainingUpdateEvent(this, title, content, emails)
        );
    }

    private String getFormattedDate(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }

    @Override
    public void sendNotificationsAndEmailsWhenRemovingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> userDocumentList
    ) {
        //todo add implementation
        throw new UnsupportedOperationException();
    }
}
