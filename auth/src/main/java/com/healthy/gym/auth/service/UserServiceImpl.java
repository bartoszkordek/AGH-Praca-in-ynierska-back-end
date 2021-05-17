package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.entity.ResetPasswordToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.data.repository.UserDAO;
import com.healthy.gym.auth.events.OnResetPasswordEvent;
import com.healthy.gym.auth.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TokenService tokenService;

    @Autowired
    public UserServiceImpl(
            UserDAO userDAO,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            ApplicationEventPublisher applicationEventPublisher,
            TokenService tokenService
    ) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.applicationEventPublisher = applicationEventPublisher;
        this.tokenService = tokenService;
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
        userEntity.setAccountNonExpired(true);
        userEntity.setCredentialsNonExpired(true);
        userEntity.setAccountNonLocked(true);

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
    public ResetPasswordToken resetPassword(String email) {

        UserEntity userEntity = userDAO.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException("No user found");

        if (!userEntity.isAccountNonExpired())
            throw new AccountExpiredException("Account expired");

        if (!userEntity.isCredentialsNonExpired())
            throw new CredentialsExpiredException("Credentials expired.");

        if (!userEntity.isEnabled())
            throw new DisabledException("User is not enabled");

        if (!userEntity.isAccountNonLocked())
            throw new LockedException("Account locked");

        ResetPasswordToken resetPasswordToken = tokenService.createResetPasswordToken(userEntity);

        applicationEventPublisher.publishEvent(new OnResetPasswordEvent(resetPasswordToken));

        return resetPasswordToken;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userDAO.findByEmail(email);

        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(
                userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.isEnabled(),
                userEntity.isAccountNonExpired(),
                userEntity.isCredentialsNonExpired(),
                userEntity.isAccountNonLocked(),
                new ArrayList<>()
        );
    }
}
