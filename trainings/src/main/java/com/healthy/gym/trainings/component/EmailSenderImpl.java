package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.configuration.EmailConfiguration;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailSenderImpl implements EmailSender {

    private final EmailConfiguration emailConfig;

    @Autowired
    public EmailSenderImpl(EmailConfiguration emailConfig) {
        this.emailConfig = emailConfig;
    }

    @Override
    public void sendEmailWithoutAttachment(List<String> recipients, String subject, String body) {
        String fromEmail = emailConfig.getMailUsername();
        String personal = emailConfig.getEmailPersonal();
        String password = emailConfig.getMailPassword();
        String filePath = null;
        EmailSendModel emailSendModel = new EmailSendModel(
                fromEmail,
                personal,
                recipients,
                password,
                subject,
                body,
                filePath
        );
        EmailService emailService = new EmailService();
        String host = emailConfig.getMailHost();
        String port = emailConfig.getMailPort();
        emailService.overrideDefaultSmptCredentials(host, port);
        emailService.sendEmailTLS(emailSendModel);
    }
}
