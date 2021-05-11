package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
import com.healthy.gym.user.data.repository.ResetPasswordTokenDAO;
import com.healthy.gym.user.data.repository.UserDAO;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final UserDAO userDAO;
    private final RegistrationTokenDAO registrationTokenDAO;
    private final ResetPasswordTokenDAO resetPasswordTokenDAO;

    @Autowired
    public TokenServiceImpl(
            UserDAO userDAO,
            RegistrationTokenDAO registrationTokenDAO,
            ResetPasswordTokenDAO resetPasswordTokenDAO
    ) {
        this.userDAO = userDAO;
        this.registrationTokenDAO = registrationTokenDAO;
        this.resetPasswordTokenDAO = resetPasswordTokenDAO;
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
    public void verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException {
        RegistrationToken registrationToken = registrationTokenDAO.findByToken(token);

        if (registrationToken == null) throw new InvalidTokenException();
        if (tokenExpired(registrationToken)) throw new ExpiredTokenException();

        UserEntity userEntity = registrationToken.getUserEntity();
        if (userEntity == null) throw new IllegalStateException();
        userEntity.setEnabled(true);

        userDAO.save(userEntity);
    }

    private boolean tokenExpired(RegistrationToken registrationToken) {
        return registrationToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    public void verifyResetPasswordToken(String token) throws InvalidTokenException, ExpiredTokenException {

    }
}
