package com.healthy.gym.auth.component;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;

public interface MailMessageManager {
    String getConfirmRegistrationMessageSubject();

    String getConfirmRegistrationMessageText(RegistrationTokenDocument registrationToken);

    String getResetPasswordMessageSubject();

    String getResetPasswordMessageText(ResetPasswordTokenDocument resetPasswordToken);
}
