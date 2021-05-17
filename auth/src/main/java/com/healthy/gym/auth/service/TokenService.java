package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.entity.RegistrationToken;
import com.healthy.gym.auth.data.entity.ResetPasswordToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.exceptions.token.ExpiredTokenException;
import com.healthy.gym.auth.exceptions.token.InvalidTokenException;
import com.healthy.gym.auth.shared.UserDTO;

public interface TokenService {

    ResetPasswordToken createResetPasswordToken(UserEntity userEntity) throws IllegalStateException;

    RegistrationToken createRegistrationToken(UserDTO user, String token);

    UserDTO verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException;

    UserDTO verifyTokenAndResetPassword(String token, String newPassword) throws InvalidTokenException, ExpiredTokenException;
}
