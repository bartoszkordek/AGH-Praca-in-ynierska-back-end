package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.NotificationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.NotificationDAO;
import com.healthy.gym.trainings.events.OnGroupTrainingChangeEvent;
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
        String content = translator.toLocale("notification.group.training.update");
        sendNotifications(users, shouldSendEmails, trainingName, startDateTime, content);
    }

    private String getFormattedDate(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }

    private void sendNotifications(
            List<UserDocument> users,
            boolean shouldSendEmails,
            String trainingName,
            LocalDateTime startDateTime,
            String content
    ) {
        String title = trainingName + " " + getFormattedDate(startDateTime);

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
                new OnGroupTrainingChangeEvent(this, title, content, emails)
        );
    }

    @Override
    public void sendNotificationsAndEmailsWhenRemovingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> users,
            boolean shouldSendEmails
    ) {
        String content = translator.toLocale("notification.group.training.cancelled");
        sendNotifications(users, shouldSendEmails, trainingName, startDateTime, content);
    }

    @Override
    public void sendNotificationWhenCreateIndividualTrainingRequest(
            UserDocument toTrainer,
            UserDocument fromUser,
            LocalDateTime startDateTime
    ) {
        String title = "Zapytanie o trening indywidualny";
        String content = "Użytkownik " + fromUser.getName() + " " + fromUser.getSurname() +
                " prosi o trening indywidualny (" + getFormattedDate(startDateTime) + ").";

        var notification = new NotificationDocument(toTrainer, title, content);
        notificationDAO.save(notification);
    }

    @Override
    public void sendNotificationWhenCancelIndividualTrainingRequest(
            UserDocument toTrainer,
            UserDocument fromUser,
            LocalDateTime startDateTime
    ) {
        String title = "Rezygnacja z treningu indywidualnego";
        String content = "Użytkownik " + fromUser.getName() + " " + fromUser.getSurname() +
                " zrezygnował z  trening indywidualnego (" + getFormattedDate(startDateTime) + ").";

        var notification = new NotificationDocument(toTrainer, title, content);
        notificationDAO.save(notification);
    }
}
