package com.healthy.gym.auth.events;

import com.healthy.gym.auth.shared.UserDTO;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final UserDTO userDTO;

    public OnRegistrationCompleteEvent(UserDTO userDTO) {
        super(userDTO);
        this.userDTO = userDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }
}
