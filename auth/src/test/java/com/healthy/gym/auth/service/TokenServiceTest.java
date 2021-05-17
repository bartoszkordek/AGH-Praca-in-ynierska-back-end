package com.healthy.gym.auth.service;

import com.healthy.gym.auth.data.entity.RegistrationToken;
import com.healthy.gym.auth.data.entity.ResetPasswordToken;
import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.data.repository.RegistrationTokenDAO;
import com.healthy.gym.auth.data.repository.ResetPasswordTokenDAO;
import com.healthy.gym.auth.data.repository.UserDAO;
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
    private UserEntity janKowalskiEntity;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        janKowalskiEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString(),
                true
        );

        userDTO = new UserDTO(
                janKowalskiEntity.getUserId(),
                janKowalskiEntity.getName(),
                janKowalskiEntity.getSurname(),
                janKowalskiEntity.getEmail(),
                janKowalskiEntity.getPhoneNumber(),
                "password1234",
                janKowalskiEntity.getEncryptedPassword()
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
                    () -> tokenService.createResetPasswordToken(new UserEntity())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldRestPasswordTokenWhenValidEntityProvided() {
            janKowalskiEntity.setId(1L);
            when(resetPasswordTokenDAO.save(any())).thenReturn(new ResetPasswordToken(token, janKowalskiEntity));

            ResetPasswordToken resetPasswordToken = tokenService.createResetPasswordToken(janKowalskiEntity);

            assertThat(resetPasswordToken.getToken()).isEqualTo(token);
            assertThat(resetPasswordToken.getUserEntity()).isEqualTo(janKowalskiEntity);
        }
    }

    @Nested
    class WhenCreateRegistrationTokenIsCalled {
        private RegistrationToken registrationToken;

        @BeforeEach
        void setUp() {
            when(registrationTokenDAO.save(any(RegistrationToken.class)))
                    .thenReturn(new RegistrationToken(token, janKowalskiEntity));

            registrationToken = tokenService.createRegistrationToken(userDTO, token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperToken() {
            assertThat(registrationToken.getToken()).isEqualTo(token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperUser() {
            assertThat(registrationToken.getUserEntity()).isEqualTo(janKowalskiEntity);
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
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
            resetPasswordToken.setWasUsed(true);

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            ResetPasswordToken expiredToken = new ResetPasswordToken();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithResetPasswordToken() {
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserEntity(null);

            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);

            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenResetPasswordTokenHaveNotBeenUpdatedProperly() throws ExpiredTokenException, InvalidTokenException {
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserEntity(janKowalskiEntity);

            String newPassword = "newTestPassword";
            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            when(userDAO.save(janKowalskiEntity)).thenReturn(
                    new UserEntity(
                            janKowalskiEntity.getName(),
                            janKowalskiEntity.getSurname(),
                            janKowalskiEntity.getEmail(),
                            janKowalskiEntity.getPhoneNumber(),
                            bCryptPasswordEncoder.encode(newPassword),
                            janKowalskiEntity.getUserId(),
                            janKowalskiEntity.isEnabled(),
                            janKowalskiEntity.isAccountNonExpired(),
                            janKowalskiEntity.isCredentialsNonExpired(),
                            janKowalskiEntity.isAccountNonLocked()
                    )
            );

            ResetPasswordToken savedResetPasswordToken = new ResetPasswordToken();
            savedResetPasswordToken.setWasUsed(false);
            when(resetPasswordTokenDAO.save(resetPasswordToken)).thenReturn(savedResetPasswordToken);

            assertThatThrownBy(
                    () -> tokenService.verifyTokenAndResetPassword("testToken", anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldReturnUserDTOWithNewEncryptedPassword() throws ExpiredTokenException, InvalidTokenException {
            ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
            resetPasswordToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            resetPasswordToken.setUserEntity(janKowalskiEntity);

            String newPassword = "newTestPassword";
            when(resetPasswordTokenDAO.findByToken("testToken")).thenReturn(resetPasswordToken);
            when(userDAO.save(janKowalskiEntity)).thenReturn(
                    new UserEntity(
                            janKowalskiEntity.getName(),
                            janKowalskiEntity.getSurname(),
                            janKowalskiEntity.getEmail(),
                            janKowalskiEntity.getPhoneNumber(),
                            bCryptPasswordEncoder.encode(newPassword),
                            janKowalskiEntity.getUserId(),
                            janKowalskiEntity.isEnabled(),
                            janKowalskiEntity.isAccountNonExpired(),
                            janKowalskiEntity.isCredentialsNonExpired(),
                            janKowalskiEntity.isAccountNonLocked()
                    )
            );

            ResetPasswordToken savedResetPasswordToken = new ResetPasswordToken();
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
            RegistrationToken registrationToken = new RegistrationToken();
            registrationToken.setWasUsed(true);

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            RegistrationToken expiredToken = new RegistrationToken();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithRegisterToken() {
            RegistrationToken registrationToken = spy(RegistrationToken.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            when(registrationToken.getUserEntity()).thenReturn(null);

            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenRegistrationTokenHaveNotBeenUpdatedProperly() throws ExpiredTokenException, InvalidTokenException {
            janKowalskiEntity.setEnabled(false);

            RegistrationToken registrationToken = spy(RegistrationToken.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            registrationToken.setUserEntity(janKowalskiEntity);

            when(registrationTokenDAO.findByToken("testToken")).thenReturn(registrationToken);
            when(userDAO.save(janKowalskiEntity)).thenReturn(
                    new UserEntity(
                            janKowalskiEntity.getName(),
                            janKowalskiEntity.getSurname(),
                            janKowalskiEntity.getEmail(),
                            janKowalskiEntity.getPhoneNumber(),
                            janKowalskiEntity.getEncryptedPassword(),
                            janKowalskiEntity.getUserId(),
                            !janKowalskiEntity.isEnabled(),
                            janKowalskiEntity.isAccountNonExpired(),
                            janKowalskiEntity.isCredentialsNonExpired(),
                            janKowalskiEntity.isAccountNonLocked()
                    )
            );

            RegistrationToken savedRegistrationToken = new RegistrationToken();
            savedRegistrationToken.setWasUsed(false);
            when(registrationTokenDAO.save(registrationToken)).thenReturn(savedRegistrationToken);

            assertThat(registrationToken.getUserEntity().isEnabled()).isFalse();

            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken("testToken")
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldReturnUserDTOWithAccountEnabled() throws ExpiredTokenException, InvalidTokenException {
            janKowalskiEntity.setEnabled(false);

            RegistrationToken registrationToken = spy(RegistrationToken.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));
            registrationToken.setUserEntity(janKowalskiEntity);

            when(registrationTokenDAO.findByToken("testToken")).thenReturn(registrationToken);
            when(userDAO.save(janKowalskiEntity)).thenReturn(
                    new UserEntity(
                            janKowalskiEntity.getName(),
                            janKowalskiEntity.getSurname(),
                            janKowalskiEntity.getEmail(),
                            janKowalskiEntity.getPhoneNumber(),
                            janKowalskiEntity.getEncryptedPassword(),
                            janKowalskiEntity.getUserId(),
                            !janKowalskiEntity.isEnabled(),
                            janKowalskiEntity.isAccountNonExpired(),
                            janKowalskiEntity.isCredentialsNonExpired(),
                            janKowalskiEntity.isAccountNonLocked()
                    )
            );

            RegistrationToken savedRegistrationToken = new RegistrationToken();
            savedRegistrationToken.setWasUsed(true);
            when(registrationTokenDAO.save(registrationToken)).thenReturn(savedRegistrationToken);

            assertThat(registrationToken.getUserEntity().isEnabled()).isFalse();

            UserDTO userDTO = tokenService.verifyRegistrationToken("testToken");

            assertThat(userDTO.isEnabled()).isTrue();
        }
    }

}