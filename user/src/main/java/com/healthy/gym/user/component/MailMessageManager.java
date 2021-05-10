package com.healthy.gym.user.component;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;

public interface MailMessageManager {
    String getConfirmRegistrationTextMessage(RegistrationToken registrationToken);

    String getResetPasswordTextMessage(ResetPasswordToken resetPasswordToken);
}
