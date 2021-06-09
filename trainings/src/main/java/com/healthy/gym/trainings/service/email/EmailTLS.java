package com.healthy.gym.trainings.service.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import java.util.List;
import java.util.Properties;

public class EmailTLS {

    public static void sendEmail(String fromEmail, String personal, List<String> toEmails, String password, String subject,
                                     String body, String filePath, String smtpHost, String smtpPort) {

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost); //SMTP Host
            props.put("mail.smtp.port", smtpPort); //TLS Port
            props.put("mail.smtp.auth", "true"); //enable authentication
            props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
            props.put("mail.smtp.ssl.trust", smtpHost); //trust Host

            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            };
            Session session = Session.getInstance(props, auth);

            EmailUtil.sendEmail(session, fromEmail, personal, toEmails, subject, body, filePath);

    }
}
