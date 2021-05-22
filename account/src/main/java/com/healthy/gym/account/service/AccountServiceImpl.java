package com.healthy.gym.account.service;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserDAO userDAO;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AccountServiceImpl(UserDAO userDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public UserDTO changePassword(String userId, String oldPassword, String newPassword)
            throws OldPasswordDoesNotMatchException, IdenticalOldAndNewPasswordException {
        UserDocument foundUser = userDAO.findByUserId(userId);
        if (foundUser == null) throw new UsernameNotFoundException("User not found.");
        validateIfOldPasswordMatches(oldPassword, foundUser);
        validateIfNewPasswordIsNotEqualToOldPassword(newPassword, foundUser);

        foundUser.setEncryptedPassword(getNewEncryptedPassword(newPassword));
        UserDocument updateUser = userDAO.save(foundUser);
        return modelMapper.map(updateUser, UserDTO.class);
    }

    private void validateIfOldPasswordMatches(String oldPassword, UserDocument foundUser)
            throws OldPasswordDoesNotMatchException {
        String encryptedPassword = foundUser.getEncryptedPassword();
        boolean matches = bCryptPasswordEncoder.matches(oldPassword, encryptedPassword);
        if (!matches) throw new OldPasswordDoesNotMatchException();
    }

    private void validateIfNewPasswordIsNotEqualToOldPassword(String newPassword, UserDocument foundUser)
            throws IdenticalOldAndNewPasswordException {
        String encryptedPassword = foundUser.getEncryptedPassword();
        boolean matches = bCryptPasswordEncoder.matches(newPassword, encryptedPassword);
        if (matches) throw new IdenticalOldAndNewPasswordException();
    }

    private String getNewEncryptedPassword(String newPassword) {
        return bCryptPasswordEncoder.encode(newPassword);
    }

    @Override
    public UserDTO deleteAccount(String userId) {
        UserDocument foundUser = userDAO.findByUserId(userId);
        if (foundUser == null) throw new UsernameNotFoundException("User not found.");
        userDAO.delete(foundUser);
        return modelMapper.map(foundUser, UserDTO.class);
    }
}
