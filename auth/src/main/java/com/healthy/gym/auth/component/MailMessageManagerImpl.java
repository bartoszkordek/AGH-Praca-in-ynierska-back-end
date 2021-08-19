package com.healthy.gym.auth.component;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

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
    public String getConfirmRegistrationMessageText(RegistrationTokenDocument registrationToken) {
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
    public String getResetPasswordMessageText(ResetPasswordTokenDocument resetPasswordToken) {
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
        return getBaseUrl() + "/confirmNewPassword?token=" + token;
    }

    private String getBaseUrl() {
        String protocol = environment.getRequiredProperty("front-end.protocol");
        String host = environment.getRequiredProperty("front-end.host");
        String port = environment.getRequiredProperty("front-end.port");

        return protocol + "://" + host + ":" + port + getHomePage();
    }

    private String getHomePage() {
        boolean isDockerProfile = Set.of(environment.getActiveProfiles()).contains("docker");
        if (isDockerProfile) return "";

        String homepage = environment.getRequiredProperty("front-end.homepage");
        return "/" + homepage;
    }
}
