package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.DetailUserInfoDTO;
import com.healthy.gym.account.enums.GymRole;
import com.healthy.gym.account.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    public ManagerServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public DetailUserInfoDTO changeUserRoles(String userId, List<String> roles) throws UserNotFoundException {

        UserDocument userDocument = userDAO.findByUserId(userId);
        if (userDocument == null) throw new UserNotFoundException();

        Set<GymRole> gymRoles = roles
                .stream()
                .map(String::toUpperCase)
                .map(GymRole::valueOf)
                .collect(Collectors.toSet());

        gymRoles.add(GymRole.USER);
        userDocument.setGymRoles(gymRoles);

        var updatedUser = userDAO.save(userDocument);
        return modelMapper.map(updatedUser, DetailUserInfoDTO.class);
    }
}
