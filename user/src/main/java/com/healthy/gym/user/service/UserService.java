package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    String status();

    UserDTO createUser(UserDTO userDetails);

    UserDTO getUserDetailsByEmail(String email);

    RegistrationToken createRegistrationToken(UserDTO user, String token);

    void verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException;
}
