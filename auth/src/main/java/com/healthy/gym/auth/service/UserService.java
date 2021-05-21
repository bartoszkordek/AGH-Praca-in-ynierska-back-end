package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.shared.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    String status();

    UserDTO createUser(UserDTO userDetails);

    UserDTO getUserDetailsByEmail(String email);

    ResetPasswordTokenDocument resetPassword(String email);
}
