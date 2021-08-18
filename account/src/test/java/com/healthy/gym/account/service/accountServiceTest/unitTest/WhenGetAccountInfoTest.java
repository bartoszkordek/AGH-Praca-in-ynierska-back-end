package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenGetAccountInfoTest {

    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private String userId;

    @Autowired
    private AccountService accountService;

    @MockBean
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        andrzejNowakDTO = new UserDTO(
                userId,
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                null,
                null
        );

        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                null,
                userId
        );

        doNothing().when(userDAO).delete(any(UserDocument.class));
    }

    @Test
    void shouldGetAccountInfoWhenProvidedValidUserId() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        assertThat(accountService.getAccountInfo(userId)).isEqualTo(andrzejNowakDTO);
    }

    @Test
    void shouldThrowExceptionWhenProvidedUserIdIsInvalidOrDoestNotExist() {
        when(userDAO.findByUserId(userId)).thenThrow(UsernameNotFoundException.class);
        assertThatThrownBy(() -> accountService.getAccountInfo(userId))
                .isInstanceOf(UsernameNotFoundException.class);

    }
}
