package com.healthy.gym.trainings.service;

import com.healthy.gym.trainings.component.Translator;
import com.healthy.gym.trainings.data.document.NotificationDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.data.repository.NotificationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDAO notificationDAO;
    private final Translator translator;
    private final JavaMailSender javaMailSender;

    @Autowired
    public NotificationServiceImpl(
            NotificationDAO notificationDAO,
            Translator translator,
            JavaMailSender javaMailSender
    ) {
        this.notificationDAO = notificationDAO;
        this.translator = translator;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendNotificationsAndEmailsWhenUpdatingGroupTraining(
            String trainingName,
            LocalDateTime startDateTime,
            List<UserDocument> users
    ) {
        String title = trainingName + " " + getFormattedDate(startDateTime);
        String content = translator.toLocale("notification.group.training.update");
        var notifications = users
                .stream()
                .map(user -> new NotificationDocument(user, title, content))
                .collect(Collectors.toList());
        notificationDAO.saveAll(notifications);

        users.forEach(userDocument -> {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(userDocument.getEmail());
            mail.setSubject(title);
            mail.setText(content);
            javaMailSender.send(mail);
        });
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
