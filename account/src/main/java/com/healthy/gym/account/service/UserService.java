package com.healthy.gym.account.service;

import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.exception.NoUserFound;

import java.util.List;

public interface UserService {

    List<DetailUserInfoDTO> getAllUsersInSystem() throws NoUserFound;

    List<DetailUserInfoDTO> getAllTrainersInSystem() throws NoUserFound;

    List<DetailUserInfoDTO> getAllEmployeesInSystem() throws NoUserFound;

    List<DetailUserInfoDTO> getAllManagersInSystem() throws NoUserFound;

}
