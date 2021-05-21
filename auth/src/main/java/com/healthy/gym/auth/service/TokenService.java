package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.exceptions.token.ExpiredTokenException;
import com.healthy.gym.auth.exceptions.token.InvalidTokenException;
import com.healthy.gym.auth.shared.UserDTO;

public interface TokenService {

    ResetPasswordTokenDocument createResetPasswordToken(UserDocument userDocument) throws IllegalStateException;

    RegistrationTokenDocument createRegistrationToken(UserDTO user, String token);

    UserDTO verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException;

    UserDTO verifyTokenAndResetPassword(String token, String newPassword) throws InvalidTokenException, ExpiredTokenException;
}
