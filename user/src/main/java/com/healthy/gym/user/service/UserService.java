package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.shared.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    String status();

    UserDTO createUser(UserDTO userDetails);

    UserDTO getUserDetailsByEmail(String email);

    ResetPasswordToken resetPassword(String email);
}
