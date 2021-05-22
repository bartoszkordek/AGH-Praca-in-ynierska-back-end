package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenChangePasswordTest {

    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private UserDocument andrzejNowakUpdated;
    private String userId;

    @Autowired
    private AccountService accountService;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                userId
        );

        String encryptedPasswordUpdated = bCryptPasswordEncoder.encode("password45768");

        andrzejNowakDTO = new UserDTO(
                userId,
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                null,
                encryptedPasswordUpdated
        );

        andrzejNowakUpdated = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                encryptedPasswordUpdated,
                userId
        );
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.changePassword(userId, "test1234", "test12345")
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenOldPasswordDoesNotMatch() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        assertThatThrownBy(
                () -> accountService.changePassword(userId, "test1234", "test12345")
        ).isInstanceOf(OldPasswordDoesNotMatchException.class);
    }

    @Test
    void shouldThrowExceptionWhenNewPasswordIsEqualToOldPassword() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        assertThatThrownBy(
                () -> accountService.changePassword(userId, "password4576", "password4576")
        ).isInstanceOf(IdenticalOldAndNewPasswordException.class);
    }

    @Test
    void shouldUpdatePassword()
            throws OldPasswordDoesNotMatchException, IdenticalOldAndNewPasswordException {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        when(userDAO.save(any())).thenReturn(andrzejNowakUpdated);
        assertThat(accountService.changePassword(userId, "password4576", "password45768"))
                .isEqualTo(andrzejNowakDTO);
    }
}
