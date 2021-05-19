package com.healthy.gym.account.service;

import com.healthy.gym.account.shared.UserDTO;

public interface AccountService {
    UserDTO changePassword(String password);

    UserDTO deleteAccount(String userId);
}
