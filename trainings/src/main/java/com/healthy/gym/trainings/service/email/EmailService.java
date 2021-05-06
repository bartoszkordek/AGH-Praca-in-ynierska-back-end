package com.healthy.gym.trainings.service.email;

import com.healthy.gym.trainings.model.EmailSendModel;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendEmailTLS(EmailSendModel sendEmailRequest) {
        EmailTLS.sendEmail(sendEmailRequest.getFromEmail(), sendEmailRequest.getPersonal(), sendEmailRequest.getToEmail(),
                sendEmailRequest.getPassword(), sendEmailRequest.getSubject(), sendEmailRequest.getBody(), sendEmailRequest.getFilePath());
    }
}
