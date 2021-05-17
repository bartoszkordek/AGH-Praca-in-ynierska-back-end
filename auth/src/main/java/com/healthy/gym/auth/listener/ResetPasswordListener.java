package com.healthy.gym.auth.listener;

import com.healthy.gym.auth.component.MailMessageManager;
import com.healthy.gym.auth.data.entity.ResetPasswordToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.events.OnResetPasswordEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordListener {

    private final JavaMailSender javaMailSender;
    private final MailMessageManager mailMessageManager;

    @Autowired
    public ResetPasswordListener(
            @Qualifier("getJavaMailSender") JavaMailSender javaMailSender,
            MailMessageManager mailMessageManager
    ) {
        this.javaMailSender = javaMailSender;
        this.mailMessageManager = mailMessageManager;
    }

    @Async
    @EventListener
    public void sendEmailToResetPassword(OnResetPasswordEvent event) {

        ResetPasswordToken resetPasswordToken = event.getResetPasswordToken();

        UserEntity user = resetPasswordToken.getUserEntity();
        String token = resetPasswordToken.getToken();

        if (user == null || token == null) throw new IllegalStateException();

        SimpleMailMessage resetPasswordMail = getResetPasswordMail(user, resetPasswordToken);
        javaMailSender.send(resetPasswordMail);
    }

    private SimpleMailMessage getResetPasswordMail(UserEntity user, ResetPasswordToken resetPasswordToken) {
        String recipientAddress = user.getEmail();
        String subject = mailMessageManager.getResetPasswordMessageSubject();
        String text = mailMessageManager.getResetPasswordMessageText(resetPasswordToken);

        SimpleMailMessage confirmationEmail = new SimpleMailMessage();
        confirmationEmail.setTo(recipientAddress);
        confirmationEmail.setSubject(subject);
        confirmationEmail.setText(text);

        return confirmationEmail;
    }
}
