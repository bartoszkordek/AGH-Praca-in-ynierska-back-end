package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.listener.ResetPasswordListener;
import com.healthy.gym.auth.service.TokenService;
import com.healthy.gym.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceWhenResetPasswordIsCalledTest {

    private ResetPasswordTokenDocument resetPasswordToken;
    private UserDocument janKowalski;
    private String token;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private ResetPasswordListener resetPasswordListener;

    @MockBean
    private TokenService tokenService;

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
        janKowalski.setAccountNonExpired(true);
        janKowalski.setAccountNonLocked(true);
        janKowalski.setCredentialsNonExpired(true);

        doNothing().when(resetPasswordListener).sendEmailToResetPassword(any());
        token = UUID.randomUUID().toString();
        when(tokenService.createResetPasswordToken(any()))
                .thenReturn(new ResetPasswordTokenDocument(token, janKowalski));
    }

    @Test
    void shouldThrowExceptionWhenNoUserFound() {
        when(userDAO.findByEmail(anyString())).thenReturn(null);
        assertThatThrownBy(
                () -> userService.resetPassword("jan.kowalski@test.com")
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenAccountIsExpired() {
        janKowalski.setAccountNonExpired(false);
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        assertThatThrownBy(
                () -> userService.resetPassword("jan.kowalski@test.com")
        ).isInstanceOf(AccountExpiredException.class);
    }

    @Test
    void shouldThrowExceptionWhenCredentialsExpired() {
        janKowalski.setCredentialsNonExpired(false);
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        assertThatThrownBy(
                () -> userService.resetPassword("jan.kowalski@test.com")
        ).isInstanceOf(CredentialsExpiredException.class);
    }

    @Test
    void shouldThrowExceptionWhenAccountIsDisabled() {
        janKowalski.setEnabled(false);
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        assertThatThrownBy(
                () -> userService.resetPassword("jan.kowalski@test.com")
        ).isInstanceOf(DisabledException.class);
    }

    @Test
    void shouldThrowExceptionWhenAccountIsLocked() {
        janKowalski.setAccountNonLocked(false);
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        assertThatThrownBy(
                () -> userService.resetPassword("jan.kowalski@test.com")
        ).isInstanceOf(LockedException.class);
    }

    @Test
    void shouldReturnProperEntity() {
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        resetPasswordToken = userService.resetPassword("jan.kowalski@test.com");
        assertThat(resetPasswordToken.getUserDocument()).isEqualTo(janKowalski);
    }

    @Test
    void shouldReturnToken() {
        when(userDAO.findByEmail("jan.kowalski@test.com")).thenReturn(janKowalski);
        resetPasswordToken = userService.resetPassword("jan.kowalski@test.com");
        Pattern uuidPattern = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");
        assertThat(resetPasswordToken.getToken()).isNotNull();
        assertThat(resetPasswordToken.getToken()).matches(uuidPattern);
        assertThat(resetPasswordToken.getToken()).isEqualTo(token);
    }

}
