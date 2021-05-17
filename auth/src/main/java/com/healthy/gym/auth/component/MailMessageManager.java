package com.healthy.gym.auth.component;

import com.healthy.gym.auth.data.entity.RegistrationToken;
import com.healthy.gym.auth.data.entity.ResetPasswordToken;

public interface MailMessageManager {
    String getConfirmRegistrationMessageSubject();

    String getConfirmRegistrationMessageText(RegistrationToken registrationToken);

    String getResetPasswordMessageSubject();

    String getResetPasswordMessageText(ResetPasswordToken resetPasswordToken);
}
