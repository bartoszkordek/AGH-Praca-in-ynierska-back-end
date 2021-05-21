package com.healthy.gym.auth.events;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import org.springframework.context.ApplicationEvent;

public class OnResetPasswordEvent extends ApplicationEvent {
    private final ResetPasswordTokenDocument resetPasswordToken;

    public OnResetPasswordEvent(ResetPasswordTokenDocument resetPasswordToken) {
        super(resetPasswordToken);
        this.resetPasswordToken = resetPasswordToken;
    }

    public ResetPasswordTokenDocument getResetPasswordToken() {
        return resetPasswordToken;
    }
}
