package com.healthy.gym.user.component;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;

public interface MailMessageManager {
    String getConfirmRegistrationMessageSubject();

    String getConfirmRegistrationMessageText(RegistrationToken registrationToken);

    String getResetPasswordMessageSubject();

    String getResetPasswordMessageText(ResetPasswordToken resetPasswordToken);
}
