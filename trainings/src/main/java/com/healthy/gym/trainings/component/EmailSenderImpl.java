package com.healthy.gym.trainings.component;

import com.healthy.gym.trainings.configuration.EmailConfig;
import com.healthy.gym.trainings.model.other.EmailSendModel;
import com.healthy.gym.trainings.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailSenderImpl implements EmailSender {

    private final EmailConfig emailConfig;

    @Autowired
    public EmailSenderImpl(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    @Override
    public void sendEmailWithoutAttachment(List<String> recipients, String subject, String body) {
        String fromEmail = emailConfig.getEmailName();
        String personal = emailConfig.getEmailPersonal();
        String password = emailConfig.getEmailPassword();
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
        String host = emailConfig.getSmtpHost();
        String port = emailConfig.getSmtpPort();
        emailService.overrideDefaultSmptCredentials(host, port);
        emailService.sendEmailTLS(emailSendModel);
    }
}
