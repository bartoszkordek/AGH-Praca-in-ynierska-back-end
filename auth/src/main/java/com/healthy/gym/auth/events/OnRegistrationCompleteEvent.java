package com.healthy.gym.auth.events;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final RegistrationTokenDocument registrationToken;

    public OnRegistrationCompleteEvent(RegistrationTokenDocument registrationToken) {
        super(registrationToken);
        this.registrationToken = registrationToken;
    }

    public RegistrationTokenDocument getRegistrationToken() {
        return registrationToken;
    }
}
