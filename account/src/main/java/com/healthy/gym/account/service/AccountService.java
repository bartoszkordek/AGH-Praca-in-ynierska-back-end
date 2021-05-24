package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.exception.UserDataNotUpdatedException;
import com.healthy.gym.account.shared.UserDTO;

public interface AccountService {
    UserDTO changePassword(String userId, String oldPassword, String newPassword)
            throws IdenticalOldAndNewPasswordException, OldPasswordDoesNotMatchException;

    UserDTO changeUserData(UserDTO userDTO) throws UserDataNotUpdatedException;

    UserDTO deleteAccount(String userId);
}
