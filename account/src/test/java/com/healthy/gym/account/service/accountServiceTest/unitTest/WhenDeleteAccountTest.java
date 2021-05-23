package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenDeleteAccountTest {

    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private String userId;

    @Autowired
    private AccountService accountService;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
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
        when(userDAO.findByUserId(userId)).thenThrow(UsernameNotFoundException.class);
        assertThatThrownBy(() -> accountService.deleteAccount(userId))
                .isInstanceOf(UsernameNotFoundException.class);

    }
}