package com.healthy.gym.trainings.listener;

import com.healthy.gym.trainings.component.EmailMessageManager;
import com.healthy.gym.trainings.events.OnChangeStartTimeOfGroupTrainingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ChangeStartTimeOfGroupTrainingListener {
    private final JavaMailSender javaMailSender;
    private final EmailMessageManager emailMessageManager;

    @Autowired
    public ChangeStartTimeOfGroupTrainingListener(
            @Qualifier("getJavaMailSender") JavaMailSender javaMailSender,
            EmailMessageManager emailMessageManager
    ) {
        this.javaMailSender = javaMailSender;
        this.emailMessageManager = emailMessageManager;
    }

    @Async
    @EventListener
    public void sendEmail(OnChangeStartTimeOfGroupTrainingEvent event) {
        System.out.println(event);
    }
}
