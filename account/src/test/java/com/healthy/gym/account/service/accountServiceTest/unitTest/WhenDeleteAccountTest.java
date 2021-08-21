package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.UserDTO;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WhenDeleteAccountTest {

    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private String userId;
    private AccountService accountService;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userDAO = mock(UserDAO.class);
        accountService = new AccountServiceImpl(userDAO, null, bCryptPasswordEncoder);

        String encryptedPassword = bCryptPasswordEncoder.encode("password4576");
        userId = UUID.randomUUID().toString();

        andrzejNowakDTO = new UserDTO(
                userId,
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                null,
                encryptedPassword
        );

        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                encryptedPassword,
                userId
        );

        doNothing().when(userDAO).delete(any(UserDocument.class));
    }

    @Test
    void shouldDeleteAccountWhenProvidedValidUserId() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        assertThat(accountService.deleteAccount(userId)).isEqualTo(andrzejNowakDTO);
    }

    @Test
    void shouldThrowExceptionWhenProvidedUserIdIsInvalidOrDoestNotExist() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(() -> accountService.deleteAccount(userId))
                .isInstanceOf(UsernameNotFoundException.class);

    }
}