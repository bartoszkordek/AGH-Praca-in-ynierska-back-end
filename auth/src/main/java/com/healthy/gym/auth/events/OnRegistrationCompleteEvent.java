package com.healthy.gym.auth.events;

import com.healthy.gym.auth.data.entity.RegistrationToken;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final RegistrationToken registrationToken;

    public OnRegistrationCompleteEvent(RegistrationToken registrationToken) {
        super(registrationToken);
        this.registrationToken = registrationToken;
    }

    public RegistrationToken getRegistrationToken() {
        return registrationToken;
    }
}
