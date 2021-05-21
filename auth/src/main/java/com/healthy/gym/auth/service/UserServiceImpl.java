package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
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

        UserDocument userDocument = modelMapper.map(userDetails, UserDocument.class);
        userDocument.setAccountNonExpired(true);
        userDocument.setCredentialsNonExpired(true);
        userDocument.setAccountNonLocked(true);

        UserDocument userDocumentSaved = userDAO.save(userDocument);

        return modelMapper.map(userDocumentSaved, UserDTO.class);
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
        UserDocument userDocument = userDAO.findByEmail(email);

        if (userDocument == null) throw new UsernameNotFoundException(email);

        return modelMapper.map(userDocument, UserDTO.class);
    }

    @Override
    public ResetPasswordTokenDocument resetPassword(String email) {

        UserDocument userDocument = userDAO.findByEmail(email);

        if (userDocument == null)
            throw new UsernameNotFoundException("No user found");

        if (!userDocument.isAccountNonExpired())
            throw new AccountExpiredException("Account expired");

        if (!userDocument.isCredentialsNonExpired())
            throw new CredentialsExpiredException("Credentials expired.");

        if (!userDocument.isEnabled())
            throw new DisabledException("User is not enabled");

        if (!userDocument.isAccountNonLocked())
            throw new LockedException("Account locked");

        ResetPasswordTokenDocument resetPasswordToken = tokenService.createResetPasswordToken(userDocument);

        applicationEventPublisher.publishEvent(new OnResetPasswordEvent(resetPasswordToken));

        return resetPasswordToken;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDocument userDocument = userDAO.findByEmail(email);

        if (userDocument == null) throw new UsernameNotFoundException(email);

        return new User(
                userDocument.getEmail(),
                userDocument.getEncryptedPassword(),
                userDocument.isEnabled(),
                userDocument.isAccountNonExpired(),
                userDocument.isCredentialsNonExpired(),
                userDocument.isAccountNonLocked(),
                new ArrayList<>()
        );
    }
}
