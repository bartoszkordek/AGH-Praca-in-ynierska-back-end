package com.healthy.gym.user.events;

import com.healthy.gym.user.data.entity.ResetPasswordToken;
import org.springframework.context.ApplicationEvent;

public class OnResetPasswordEvent extends ApplicationEvent {
    private final ResetPasswordToken resetPasswordToken;

    public OnResetPasswordEvent(ResetPasswordToken resetPasswordToken) {
        super(resetPasswordToken);
        this.resetPasswordToken = resetPasswordToken;
    }

    public ResetPasswordToken getResetPasswordToken() {
        return resetPasswordToken;
    }
}
