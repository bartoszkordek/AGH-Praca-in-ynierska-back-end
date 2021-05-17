package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.AbstractTokenEntity;
import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
import com.healthy.gym.user.data.repository.ResetPasswordTokenDAO;
import com.healthy.gym.user.data.repository.UserDAO;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final UserDAO userDAO;
    private final RegistrationTokenDAO registrationTokenDAO;
    private final ResetPasswordTokenDAO resetPasswordTokenDAO;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public TokenServiceImpl(
            UserDAO userDAO,
            RegistrationTokenDAO registrationTokenDAO,
            ResetPasswordTokenDAO resetPasswordTokenDAO,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userDAO = userDAO;
        this.registrationTokenDAO = registrationTokenDAO;
        this.resetPasswordTokenDAO = resetPasswordTokenDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public ResetPasswordToken createResetPasswordToken(UserEntity userEntity) throws IllegalStateException {
        if (userEntity == null || userEntity.getId() == null) throw new IllegalStateException();

        String token = UUID.randomUUID().toString();

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, userEntity);
        return resetPasswordTokenDAO.save(resetPasswordToken);
    }

    @Override
    public RegistrationToken createRegistrationToken(UserDTO user, String token) {

        UserEntity userEntity = userDAO.findByEmail(user.getEmail());
        RegistrationToken verificationToken = new RegistrationToken(token, userEntity);
        return registrationTokenDAO.save(verificationToken);
    }

    @Override
    public UserDTO verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException {
        RegistrationToken registrationToken = registrationTokenDAO.findByToken(token);

        if (registrationToken == null || registrationToken.wasUsed()) throw new InvalidTokenException();
        if (tokenExpired(registrationToken)) throw new ExpiredTokenException();

        UserEntity userEntity = registrationToken.getUserEntity();
        if (userEntity == null) throw new IllegalStateException();
        userEntity.setEnabled(true);

        UserEntity savedUserEntity = userDAO.save(userEntity);
        registrationToken.setWasUsed(true);
        RegistrationToken savedRegistrationToken = registrationTokenDAO.save(registrationToken);
        if (!savedRegistrationToken.wasUsed()) throw new IllegalStateException();

        return modelMapper.map(savedUserEntity, UserDTO.class);
    }

    private boolean tokenExpired(AbstractTokenEntity tokenEntity) {
        return tokenEntity.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    public UserDTO verifyTokenAndResetPassword(String token, String newPassword) throws InvalidTokenException, ExpiredTokenException {

        ResetPasswordToken resetPasswordToken = resetPasswordTokenDAO.findByToken(token);

        if (resetPasswordToken == null || resetPasswordToken.wasUsed()) throw new InvalidTokenException();
        if (tokenExpired(resetPasswordToken)) throw new ExpiredTokenException();

        UserEntity userEntity = resetPasswordToken.getUserEntity();
        if (userEntity == null) throw new IllegalStateException();

        String newEncryptedPassword = bCryptPasswordEncoder.encode(newPassword);
        userEntity.setEncryptedPassword(newEncryptedPassword);

        UserEntity savedUserEntity = userDAO.save(userEntity);
        resetPasswordToken.setWasUsed(true);
        ResetPasswordToken savedResetPasswordToken = resetPasswordTokenDAO.save(resetPasswordToken);
        if (!savedResetPasswordToken.wasUsed()) throw new IllegalStateException();

        return modelMapper.map(savedUserEntity, UserDTO.class);
    }
}
