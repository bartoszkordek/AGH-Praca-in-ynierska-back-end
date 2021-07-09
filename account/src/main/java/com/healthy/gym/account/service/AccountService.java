package com.healthy.gym.account.service;

import com.healthy.gym.account.exception.*;
import com.healthy.gym.account.shared.UserDTO;
import com.healthy.gym.account.shared.UserPrivacyDTO;

public interface AccountService {
    UserDTO changePassword(String userId, String oldPassword, String newPassword)
            throws IdenticalOldAndNewPasswordException, OldPasswordDoesNotMatchException;

    UserDTO changeUserData(UserDTO userDTO) throws UserDataNotUpdatedException, EmailOccupiedException;

    UserDTO deleteAccount(String userId);

    UserDTO getAccountInfo(String userId);

    UserPrivacyDTO changeUserPrivacy(UserPrivacyDTO userPrivacyDTO, String userId)
            throws UserPrivacyNotUpdatedException;

    UserPrivacyDTO getUserPrivacy(String userId);
}
