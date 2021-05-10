package com.healthy.gym.user.service;

import com.healthy.gym.user.shared.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    String status();

    UserDTO createUser(UserDTO userDetails);

    UserDTO getUserDetailsByEmail(String email);

    void resetPassword(String email);
}
