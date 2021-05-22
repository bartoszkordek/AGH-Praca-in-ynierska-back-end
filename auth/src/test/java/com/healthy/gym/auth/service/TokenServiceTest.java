package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.RegistrationTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.ResetPasswordTokenDAO;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.exceptions.token.ExpiredTokenException;
import com.healthy.gym.auth.exceptions.token.InvalidTokenException;
import com.healthy.gym.auth.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private RegistrationTokenDAO registrationTokenDAO;

    @MockBean
    private ResetPasswordTokenDAO resetPasswordTokenDAO;

    @MockBean
    private UserDAO userDAO;

    private String token;
    private UserDocument janKowalski;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        janKowalski = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString()
        );
        janKowalski.setEnabled(true);

        userDTO = new UserDTO(
                janKowalski.getUserId(),
                janKowalski.getName(),
                janKowalski.getSurname(),
                janKowalski.getEmail(),
                janKowalski.getPhoneNumber(),
                "password1234",
                janKowalski.getEncryptedPassword()
        );

        token = UUID.randomUUID().toString();
    }

    @Nested
    class WhenCreateResetPasswordTokenIsCalled {

        @Test
        void shouldThrowExceptionWhenNullEntityProvided() {
            assertThatThrownBy(
                    () -> tokenService.createResetPasswordToken(null)
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowExceptionWhenInvalidEntityProvided() {
            assertThatThrownBy(
                    () -> tokenService.createResetPasswordToken(new UserDocument())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldRestPasswordTokenWhenValidEntityProvided() {
            janKowalski.setId(UUID.randomUUID().toString());
            when(resetPasswordTokenDAO.save(any())).thenReturn(new ResetPasswordTokenDocument(token, janKowalski));

            ResetPasswordTokenDocument resetPasswordToken = tokenService.createResetPasswordToken(janKowalski);

            assertThat(resetPasswordToken.getToken()).isEqualTo(token);
            assertThat(resetPasswordToken.getUserDocument()).isEqualTo(janKowalski);
        }
    }

    @Nested
    class WhenCreateRegistrationTokenIsCalled {
        private RegistrationTokenDocument registrationToken;

        @BeforeEach
        void setUp() {
            when(registrationTokenDAO.save(any(RegistrationTokenDocument.class)))
                    .thenReturn(new RegistrationTokenDocument(token, janKowalski));

            registrationToken = tokenService.createRegistrationToken(userDTO, token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperToken() {
            assertThat(registrationToken.getToken()).isEqualTo(token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperUser() {
            assertThat(registrationToken.getUserDocument()).isEqualTo(janKowalski);
        }
    }

    @Nested
    class WhenVerifyTokenAndResetPasswordIsCalled {

        @Test
        void shouldThrowInvalidTokenExceptionWhenProvidedInvalidToken() {
            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(null);
            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTheTokenWasUsed() {
            ResetPasswordTokenDocument resetPasswordToken = new ResetPasswordTokenDocument();
            resetPasswordToken.setWasUsed(true);

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            ResetPasswordTokenDocument expiredToken = new ResetPasswordTokenDocument();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithResetPasswordToken() {
            ResetPasswordTokenDocument resetPasswordToken = new ResetPasswordTokenDocument();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserDocument(null);

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);

            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenResetPasswordTokenHaveNotBeenUpdatedProperly() throws ExpiredTokenException, InvalidTokenException {
            ResetPasswordTokenDocument resetPasswordToken = new ResetPasswordTokenDocument();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserDocument(janKowalski);

            String newPassword = "newTestPassword";
            UserDocument returnUserDocument = new UserDocument(
                    janKowalski.getName(),
                    janKowalski.getSurname(),
                    janKowalski.getEmail(),
                    janKowalski.getPhoneNumber(),
                    bCryptPasswordEncoder.encode(newPassword),
                    janKowalski.getUserId()
            );
            returnUserDocument.setEnabled(janKowalski.isEnabled());
            returnUserDocument.setAccountNonExpired(janKowalski.isAccountNonExpired());
            returnUserDocument.setCredentialsNonExpired(janKowalski.isCredentialsNonExpired());
            returnUserDocument.setAccountNonLocked(janKowalski.isAccountNonLocked());

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            when(userDAO.save(janKowalski)).thenReturn(returnUserDocument);

            ResetPasswordTokenDocument savedResetPasswordToken = new ResetPasswordTokenDocument();
            savedResetPasswordToken.setWasUsed(false);
            when(resetPasswordTokenDAO.save(resetPasswordToken)).thenReturn(savedResetPasswordToken);

            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldReturnUserDTOWithNewEncryptedPassword() throws ExpiredTokenException, InvalidTokenException {
            ResetPasswordTokenDocument resetPasswordToken = new ResetPasswordTokenDocument();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserDocument(janKowalski);

            String newPassword = "newTestPassword";
            UserDocument returnUserDocument = new UserDocument(
                    janKowalski.getName(),
                    janKowalski.getSurname(),
                    janKowalski.getEmail(),
                    janKowalski.getPhoneNumber(),
                    bCryptPasswordEncoder.encode(newPassword),
                    janKowalski.getUserId()
            );
            returnUserDocument.setEnabled(janKowalski.isEnabled());
            returnUserDocument.setAccountNonExpired(janKowalski.isAccountNonExpired());
            returnUserDocument.setCredentialsNonExpired(janKowalski.isCredentialsNonExpired());
            returnUserDocument.setAccountNonLocked(janKowalski.isAccountNonLocked());

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            when(userDAO.save(janKowalski)).thenReturn(returnUserDocument);

            ResetPasswordTokenDocument savedResetPasswordToken = new ResetPasswordTokenDocument();
            savedResetPasswordToken.setWasUsed(true);
            when(resetPasswordTokenDAO.save(resetPasswordToken)).thenReturn(savedResetPasswordToken);

            UserDTO userDTO = tokenService.verifyTokenAndResetPassword("testToken", newPassword);

            assertThat(bCryptPasswordEncoder.matches(newPassword, userDTO.getEncryptedPassword())).isTrue();
        }
    }

    @Nested
    class WhenVerifyRegistrationTokenIsCalled {

        @Test
        void shouldThrowInvalidTokenExceptionWhenProvidedInvalidToken() {
            when(registrationTokenDAO.findByToken(anyString())).thenReturn(null);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTheTokenWasUsed() {
            RegistrationTokenDocument registrationToken = new RegistrationTokenDocument();
            registrationToken.setWasUsed(true);

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            RegistrationTokenDocument expiredToken = new RegistrationTokenDocument();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithRegisterToken() {
            RegistrationTokenDocument registrationToken = spy(RegistrationTokenDocument.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            when(registrationToken.getUserDocument()).thenReturn(null);

            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenRegistrationTokenHaveNotBeenUpdatedProperly() throws ExpiredTokenException, InvalidTokenException {
            janKowalski.setEnabled(false);

            RegistrationTokenDocument registrationToken = spy(RegistrationTokenDocument.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            registrationToken.setUserDocument(janKowalski);

            UserDocument returnUserDocument = new UserDocument(
                    janKowalski.getName(),
                    janKowalski.getSurname(),
                    janKowalski.getEmail(),
                    janKowalski.getPhoneNumber(),
                    janKowalski.getEncryptedPassword(),
                    janKowalski.getUserId()
            );
            returnUserDocument.setEnabled(!janKowalski.isEnabled());
            returnUserDocument.setAccountNonExpired(janKowalski.isAccountNonExpired());
            returnUserDocument.setCredentialsNonExpired(janKowalski.isCredentialsNonExpired());
            returnUserDocument.setAccountNonLocked(janKowalski.isAccountNonLocked());

            when(registrationTokenDAO.findByToken("testToken")).thenReturn(registrationToken);
            when(userDAO.save(janKowalski)).thenReturn(returnUserDocument);

            RegistrationTokenDocument savedRegistrationToken = new RegistrationTokenDocument();
            savedRegistrationToken.setWasUsed(false);
            when(registrationTokenDAO.save(registrationToken)).thenReturn(savedRegistrationToken);

            assertThat(registrationToken.getUserDocument().isEnabled()).isFalse();

            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken("testToken")
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldReturnUserDTOWithAccountEnabled() throws ExpiredTokenException, InvalidTokenException {
            janKowalski.setEnabled(false);

            RegistrationTokenDocument registrationToken = spy(RegistrationTokenDocument.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            registrationToken.setUserDocument(janKowalski);

            UserDocument returnUserDocument = new UserDocument(
                    janKowalski.getName(),
                    janKowalski.getSurname(),
                    janKowalski.getEmail(),
                    janKowalski.getPhoneNumber(),
                    janKowalski.getEncryptedPassword(),
                    janKowalski.getUserId()
            );
            returnUserDocument.setEnabled(!janKowalski.isEnabled());
            returnUserDocument.setAccountNonExpired(janKowalski.isAccountNonExpired());
            returnUserDocument.setCredentialsNonExpired(janKowalski.isCredentialsNonExpired());
            returnUserDocument.setAccountNonLocked(janKowalski.isAccountNonLocked());

            when(registrationTokenDAO.findByToken("testToken")).thenReturn(registrationToken);
            when(userDAO.save(janKowalski)).thenReturn(returnUserDocument);

            RegistrationTokenDocument savedRegistrationToken = new RegistrationTokenDocument();
            savedRegistrationToken.setWasUsed(true);
            when(registrationTokenDAO.save(registrationToken)).thenReturn(savedRegistrationToken);

            assertThat(registrationToken.getUserDocument().isEnabled()).isFalse();

            UserDTO userDTO = tokenService.verifyRegistrationToken("testToken");

            assertThat(userDTO.isEnabled()).isTrue();
        }
    }

}