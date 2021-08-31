package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.exception.UserNotFoundException;

import java.util.List;

public interface ManagerService {
    DetailUserInfoDTO changeUserRoles(String userId, List<String> roles) throws UserNotFoundException;
}
