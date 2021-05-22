package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public UserDTO changePassword(String userId, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public UserDTO deleteAccount(String userId) {
        UserDocument foundUser = userDAO.findByUserId(userId);

        if (foundUser == null) throw new UsernameNotFoundException("User not found.");

        userDAO.delete(foundUser);

        return modelMapper.map(foundUser, UserDTO.class);
    }
}
