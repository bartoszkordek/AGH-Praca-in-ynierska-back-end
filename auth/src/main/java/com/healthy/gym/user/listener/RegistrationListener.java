package com.healthy.gym.user.listener;

import com.healthy.gym.user.component.MailMessageManager;
import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.events.OnRegistrationCompleteEvent;
import com.healthy.gym.user.service.TokenService;
import com.healthy.gym.user.shared.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener {
    private final TokenService tokenService;
    private final JavaMailSender javaMailSender;
    private final MailMessageManager mailMessageManager;

    @Autowired
    public RegistrationListener(
            TokenService tokenService,
            @Qualifier("getJavaMailSender") JavaMailSender javaMailSender,
            MailMessageManager mailMessageManager
    ) {
        this.tokenService = tokenService;
        this.javaMailSender = javaMailSender;
        this.mailMessageManager = mailMessageManager;
    }

    @Async
    @EventListener
    public void sendEmailToConfirmRegistration(OnRegistrationCompleteEvent event) {
        UserDTO user = event.getUserDTO();
        String token = UUID.randomUUID().toString();

        RegistrationToken registrationToken = tokenService.createRegistrationToken(user, token);

        SimpleMailMessage confirmationEmail = getConfirmRegistrationMail(user, registrationToken);

        javaMailSender.send(confirmationEmail);
    }

    private SimpleMailMessage getConfirmRegistrationMail(UserDTO user, RegistrationToken registrationToken) {
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
