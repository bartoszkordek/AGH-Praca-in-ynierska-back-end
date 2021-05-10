package com.healthy.gym.user.component;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MailMessageManagerImpl implements MailMessageManager {

    private final Environment environment;
    private final Translator translator;

    @Autowired
    public MailMessageManagerImpl(Environment environment, Translator translator) {
        this.environment = environment;
        this.translator = translator;
    }

    @Override
    public String getConfirmRegistrationMessageSubject() {
        return translator.toLocale("mail.registration.confirmation.subject");
    }

    @Override
    public String getConfirmRegistrationMessageText(RegistrationToken registrationToken) {
        String token = registrationToken.getToken();
        LocalDateTime expiryDate = registrationToken.getExpiryDate();

        if (token == null || expiryDate == null) throw new IllegalStateException();

        String message = translator.toLocale("mail.registration.confirmation.message");
        String linkExpiresAt = translator.toLocale("mail.registration.confirmation.expiration");

        return message + "\n" + getConfirmRegistrationUrl(token) + " \n" + linkExpiresAt + " "
                + expiryDate.toLocalDate() + " " + expiryDate.toLocalTime();
    }

    @Override
    public String getResetPasswordMessageSubject() {
        return translator.toLocale("mail.reset.password.subject");
    }

    @Override
    public String getResetPasswordMessageText(ResetPasswordToken resetPasswordToken) {
        String token = resetPasswordToken.getToken();
        LocalDateTime expiryDate = resetPasswordToken.getExpiryDate();

        if (token == null || expiryDate == null) throw new IllegalStateException();

        String message = translator.toLocale("mail.reset.password.message");
        String linkExpiresAt = translator.toLocale("mail.reset.password.expiration");

        return message + "\n" + getResetPasswordUrl(token) + " \n" + linkExpiresAt + " "
                + expiryDate.toLocalDate() + " " + expiryDate.toLocalTime();
    }

    private String getConfirmRegistrationUrl(String token) {
        return getBaseUrl() + "/confirmRegistration?token=" + token;
    }

    private String getResetPasswordUrl(String token) {
        return getBaseUrl() + "/resetPassword?token=" + token;
    }

    private String getBaseUrl() {
        String protocol = environment.getRequiredProperty("front-end.protocol");
        String host = environment.getRequiredProperty("front-end.host");
        String port = environment.getRequiredProperty("front-end.port");
        String homepage = environment.getRequiredProperty("front-end.homepage");

        return protocol + "://" + host + ":" + port + "/" + homepage;
    }
}
