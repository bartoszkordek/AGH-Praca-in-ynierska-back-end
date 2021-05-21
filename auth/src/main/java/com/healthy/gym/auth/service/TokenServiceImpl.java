package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.AbstractTokenDocument;
import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.RegistrationTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.ResetPasswordTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.exceptions.token.ExpiredTokenException;
import com.healthy.gym.auth.exceptions.token.InvalidTokenException;
import com.healthy.gym.auth.shared.UserDTO;
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
    public ResetPasswordTokenDocument createResetPasswordToken(UserDocument userDocument) throws IllegalStateException {
        if (userDocument == null || userDocument.getId() == null) throw new IllegalStateException();

        String token = UUID.randomUUID().toString();

        ResetPasswordTokenDocument resetPasswordToken = new ResetPasswordTokenDocument(token, userDocument);
        return resetPasswordTokenDAO.save(resetPasswordToken);
    }

    @Override
    public RegistrationTokenDocument createRegistrationToken(UserDTO user, String token) {

        UserDocument userDocument = userDAO.findByEmail(user.getEmail());
        RegistrationTokenDocument verificationToken = new RegistrationTokenDocument(token, userDocument);
        return registrationTokenDAO.save(verificationToken);
    }

    @Override
    public UserDTO verifyRegistrationToken(String token) throws InvalidTokenException, ExpiredTokenException {
        RegistrationTokenDocument registrationToken = registrationTokenDAO.findByToken(token);

        if (registrationToken == null || registrationToken.isWasUsed()) throw new InvalidTokenException();
        if (tokenExpired(registrationToken)) throw new ExpiredTokenException();

        UserDocument userDocument = registrationToken.getUserDocument();
        if (userDocument == null) throw new IllegalStateException();
        userDocument.setEnabled(true);

        UserDocument savedUserDocument = userDAO.save(userDocument);
        registrationToken.setWasUsed(true);
        RegistrationTokenDocument savedRegistrationToken = registrationTokenDAO.save(registrationToken);
        if (!savedRegistrationToken.isWasUsed()) throw new IllegalStateException();

        return modelMapper.map(savedUserDocument, UserDTO.class);
    }

    private boolean tokenExpired(AbstractTokenDocument token) {
        return token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    public UserDTO verifyTokenAndResetPassword(String token, String newPassword) throws InvalidTokenException, ExpiredTokenException {

        ResetPasswordTokenDocument resetPasswordToken = resetPasswordTokenDAO.findByToken(token);

        if (resetPasswordToken == null || resetPasswordToken.isWasUsed()) throw new InvalidTokenException();
        if (tokenExpired(resetPasswordToken)) throw new ExpiredTokenException();

        UserDocument userDocument = resetPasswordToken.getUserDocument();
        if (userDocument == null) throw new IllegalStateException();

        String newEncryptedPassword = bCryptPasswordEncoder.encode(newPassword);
        userDocument.setEncryptedPassword(newEncryptedPassword);

        UserDocument savedUserDocument = userDAO.save(userDocument);
        resetPasswordToken.setWasUsed(true);
        ResetPasswordTokenDocument savedResetPasswordToken = resetPasswordTokenDAO.save(resetPasswordToken);
        if (!savedResetPasswordToken.isWasUsed()) throw new IllegalStateException();

        return modelMapper.map(savedUserDocument, UserDTO.class);
    }
}
