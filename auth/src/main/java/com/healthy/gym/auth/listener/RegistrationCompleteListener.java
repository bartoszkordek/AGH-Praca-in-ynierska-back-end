package com.healthy.gym.auth.listener;

import com.healthy.gym.auth.component.MailMessageManager;
import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.events.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RegistrationCompleteListener {
    private final JavaMailSender javaMailSender;
    private final MailMessageManager mailMessageManager;

    @Autowired
    public RegistrationCompleteListener(
            @Qualifier("getJavaMailSender") JavaMailSender javaMailSender,
            MailMessageManager mailMessageManager
    ) {
        this.javaMailSender = javaMailSender;
        this.mailMessageManager = mailMessageManager;
    }

    @Async
    @EventListener
    public void sendEmailToConfirmRegistration(OnRegistrationCompleteEvent event) {
        RegistrationTokenDocument registrationToken = event.getRegistrationToken();
        UserDocument user = registrationToken.getUserDocument();

        SimpleMailMessage confirmationEmail = getConfirmRegistrationMail(user, registrationToken);

        javaMailSender.send(confirmationEmail);
    }

    private SimpleMailMessage getConfirmRegistrationMail(UserDocument user, RegistrationTokenDocument registrationToken) {
        String recipientAddress = user.getEmail();

        String subject = mailMessageManager.getConfirmRegistrationMessageSubject();
        String text = mailMessageManager.getConfirmRegistrationMessageText(registrationToken);

        SimpleMailMessage confirmationEmail = new SimpleMailMessage();
        confirmationEmail.setTo(recipientAddress);
        confirmationEmail.setSubject(subject);
        confirmationEmail.setText(text);

        return confirmationEmail;
    }
}
