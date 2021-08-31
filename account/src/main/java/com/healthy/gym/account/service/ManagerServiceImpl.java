package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.DetailUserInfoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagerServiceImpl implements ManagerService {
    @Override
    public DetailUserInfoDTO changeUserRoles(String userId, List<String> roles) {
        return null;
    }
}
