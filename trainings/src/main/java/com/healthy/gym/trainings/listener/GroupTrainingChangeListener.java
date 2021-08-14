package com.healthy.gym.trainings.listener;

import com.healthy.gym.trainings.events.OnGroupTrainingChangeEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GroupTrainingChangeListener {
    private final JavaMailSender javaMailSender;

    public GroupTrainingChangeListener(@Qualifier("getJavaMailSender") JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @EventListener
    public void sendEmail(OnGroupTrainingChangeEvent event) {
        Collection<String> emails = event.getEmails();
        String title = event.getTitle();
        String content = event.getContent();

        emails.stream()
                .parallel()
                .forEach(email -> {
                    SimpleMailMessage mail = getMailMessage(email, title, content);
                    javaMailSender.send(mail);
                });
    }

    private SimpleMailMessage getMailMessage(String email, String title, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject(title);
        mail.setText(content);

        return mail;
    }
}
