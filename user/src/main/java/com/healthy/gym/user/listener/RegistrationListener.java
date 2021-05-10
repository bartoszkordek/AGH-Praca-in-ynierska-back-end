package com.healthy.gym.user.listener;

import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.events.OnRegistrationCompleteEvent;
import com.healthy.gym.user.service.TokenService;
import com.healthy.gym.user.shared.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener {
    private final TokenService tokenService;
    private final Translator translator;
    private final JavaMailSender javaMailSender;
    private final Environment environment;

    @Autowired
    public RegistrationListener(
            TokenService tokenService,
            MessageSource messageSource,
            Translator translator,
            @Qualifier("getJavaMailSender") JavaMailSender javaMailSender,
            Environment environment
    ) {
        this.tokenService = tokenService;
        this.translator = translator;
        this.javaMailSender = javaMailSender;
        this.environment = environment;
    }

    @Async
    @EventListener
    public void sendEmailToConfirmRegistration(OnRegistrationCompleteEvent event) {
        UserDTO user = event.getUserDTO();
        String token = UUID.randomUUID().toString();

        RegistrationToken registrationToken = tokenService.createRegistrationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = translator.toLocale("mail.registration.confirmation.subject");
        String confirmationUrl = getConfirmationUrl(token);
        String message = translator.toLocale("mail.registration.confirmation.message");

        SimpleMailMessage confirmationEmail = new SimpleMailMessage();
        confirmationEmail.setTo(recipientAddress);
        confirmationEmail.setSubject(subject);
        confirmationEmail.setText(getTextMessage(message, confirmationUrl, registrationToken));

        javaMailSender.send(confirmationEmail);
    }

    private String getConfirmationUrl(String token) {
        String protocol = environment.getRequiredProperty("front-end.protocol");
        String host = environment.getRequiredProperty("front-end.host");
        String port = environment.getRequiredProperty("front-end.port");
        String homepage = environment.getRequiredProperty("front-end.homepage");

        return protocol + "://" + host + ":" + port + "/" + homepage + "/confirmRegistration?token=" + token;
    }

    private String getTextMessage(String message, String confirmationUrl, RegistrationToken registrationToken) {
        String linkExpiresAt = translator.toLocale("mail.registration.confirmation.expiration");
        return message + "\n" + confirmationUrl + " \n" + linkExpiresAt + " "
                + registrationToken.getExpiryDate().toLocalDate() + " " +
                registrationToken.getExpiryDate().toLocalTime();
    }
}
