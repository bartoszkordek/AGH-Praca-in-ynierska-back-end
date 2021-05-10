package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
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
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(
            UserDAO userDAO,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RegistrationTokenDAO registrationTokenDAO
    ) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public String status() {
        return "OK";
    }

    @Override
    public UserDTO createUser(UserDTO userDetails) {

        encryptRawPassword(userDetails);
        assignRandomPublicUserId(userDetails);

        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

        UserEntity userEntitySaved = userDAO.save(userEntity);

        return modelMapper.map(userEntitySaved, UserDTO.class);
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

        return modelMapper.map(userEntity, UserDTO.class);
    }

    @Override
    public void resetPassword(String email) {

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userDAO.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.isEnabled(),
                true,
                true,
                true,
                new ArrayList<>()
        );
    }
}
