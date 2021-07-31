package com.healthy.gym.trainings.component;

import java.util.List;

public interface EmailSender {
    void sendEmailWithoutAttachment(List<String> recipients, String subject, String body);
}
