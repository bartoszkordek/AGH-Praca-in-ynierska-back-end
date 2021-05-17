package com.healthy.gym.user.events;

import com.healthy.gym.user.shared.UserDTO;
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
