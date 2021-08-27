package com.healthy.gym.account.service;

import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.exception.NoUserFound;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final String SURNAME = "surname";
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<DetailUserInfoDTO> getAllUsersInSystem() throws NoUserFound {
        var userList = userDAO.findAll(Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .filter(userDocument -> !userDocument.getEmail().equals("root"))
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllTrainersInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.TRAINER, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllEmployeesInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.EMPLOYEE, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DetailUserInfoDTO> getAllManagersInSystem() throws NoUserFound {
        var userList = userDAO
                .findAllByGymRolesContains(GymRole.MANAGER, Sort.by(SURNAME));
        if (userList.isEmpty()) throw new NoUserFound();

        return userList
                .stream()
                .map(userDocument -> modelMapper.map(userDocument, DetailUserInfoDTO.class))
                .collect(Collectors.toList());
    }
}
