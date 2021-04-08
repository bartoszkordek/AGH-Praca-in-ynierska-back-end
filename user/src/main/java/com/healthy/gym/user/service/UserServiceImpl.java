package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.UserDAO;
import com.healthy.gym.user.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String status() {
        return "OK";
    }

    @Override
    public UserDTO createUser(UserDTO userDetails) {

        encryptRawPassword(userDetails);
        assignRandomPublicUserId(userDetails);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

        userDAO.save(userEntity);

        return modelMapper.map(userEntity, UserDTO.class);
    }

    private void encryptRawPassword(UserDTO userDetails) {
        String rawPassword = userDetails.getPassword();
        String encryptedPassword = bCryptPasswordEncoder.encode(rawPassword);
        userDetails.setEncryptedPassword(encryptedPassword);
    }

    private void assignRandomPublicUserId(UserDTO userDetails) {
        String userId = UUID.randomUUID().toString();
        userDetails.setUserId(userId);
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        UserEntity userEntity = userDAO.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new ModelMapper().map(userEntity, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userDAO.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                true,
                true,
                true,
                true,
                new ArrayList<>()
        );
    }
}
