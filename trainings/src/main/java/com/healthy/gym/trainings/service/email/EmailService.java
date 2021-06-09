package com.healthy.gym.trainings.service.email;

import com.healthy.gym.trainings.model.other.EmailSendModel;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private String defaultSmptHost = "smtp.poczta.onet.pl";
    private String defaultSmptPort = "587";

    public void overrideDefaultSmptCredentials(String host, String port){
        if(host!=null && port!=null){
            defaultSmptHost = host;
            defaultSmptPort = port;
        }
    }

    public void sendEmailTLS(EmailSendModel sendEmailRequest) {
        EmailTLS.sendEmail(sendEmailRequest.getFromEmail(), sendEmailRequest.getPersonal(), sendEmailRequest.getToEmails(),
                sendEmailRequest.getPassword(), sendEmailRequest.getSubject(), sendEmailRequest.getBody(), sendEmailRequest.getFilePath(),
                defaultSmptHost, defaultSmptPort);
    }
}
