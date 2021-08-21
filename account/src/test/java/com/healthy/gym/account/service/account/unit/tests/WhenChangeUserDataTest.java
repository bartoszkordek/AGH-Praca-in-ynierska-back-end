package com.healthy.gym.account.service.account.unit.tests;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.dto.UserDTO;
import com.healthy.gym.account.exception.EmailOccupiedException;
import com.healthy.gym.account.exception.UserDataNotUpdatedException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenChangeUserDataTest {
    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private UserDocument andrzejNowakUpdated;
    private String userId;
    private AccountService accountService;
    private UserDAO userDAO;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();

        userDAO = mock(UserDAO.class);
        accountService = new AccountServiceImpl(userDAO, null, bCryptPasswordEncoder);

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
                "Krzysztof",
                "Kowalski",
                "andrzej.nowak@test.pl",
                "676 777 999",
                null,
                encryptedPasswordUpdated
        );

        andrzejNowakUpdated = new UserDocument(
                "Krzysztof",
                "Kowalski",
                "andrzej.nowak@test.pl",
                "676 777 999",
                encryptedPasswordUpdated,
                userId
        );
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.changeUserData(andrzejNowakDTO)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenProvidedExistingEmail() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        UserDocument existingUser = new UserDocument();
        existingUser.setUserId(UUID.randomUUID().toString());
        when(userDAO.findByEmail(any())).thenReturn(existingUser);
        assertThatThrownBy(
                () -> accountService.changeUserData(andrzejNowakDTO)
        ).isInstanceOf(EmailOccupiedException.class);
    }

    @Test
    void shouldThrowExceptionWhenUpdatedUserDoesNotMatchProvidedUser() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        when(userDAO.findByEmail(any())).thenReturn(andrzejNowak);
        UserDocument userNotUpdated = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                userId
        );
        when(userDAO.save(any())).thenReturn(userNotUpdated);
        assertThatThrownBy(
                () -> accountService.changeUserData(andrzejNowakDTO)
        ).isInstanceOf(UserDataNotUpdatedException.class);
    }

    @Nested
    class ShouldThrowExceptionWhenUpdatedUserDoesNotMatchProvidedUser {
        private UserDocument userNotUpdated;

        @BeforeEach
        void setUp() {
            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            when(userDAO.findByEmail(any())).thenReturn(andrzejNowak);
            userNotUpdated = new UserDocument(
                    "Krzysztof",
                    "Kowalski",
                    "andrzej.nowak@test.pl",
                    "676 777 999",
                    bCryptPasswordEncoder.encode("password4576"),
                    userId
            );
            when(userDAO.save(any())).thenReturn(userNotUpdated);
        }

        @Test
        void WhenNameHasNotBeenUpdated() {
            userNotUpdated.setName("Andrzej");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenSurnameHasNotBeenUpdated() {
            userNotUpdated.setSurname("Nowak");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenEmailHasNotBeenUpdated() {
            userNotUpdated.setEmail("andrzej.nowak@test.com");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenPhoneHasNotBeenUpdated() {
            userNotUpdated.setPhoneNumber("676 777 888");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }
    }

    @Nested
    class ShouldUpdateUserData {
        private UserDTO user;

        @BeforeEach
        void setUp() throws UserDataNotUpdatedException, EmailOccupiedException {
            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            when(userDAO.findByEmail(any())).thenReturn(andrzejNowak);
            when(userDAO.save(any())).thenReturn(andrzejNowakUpdated);
            user = accountService.changeUserData(andrzejNowakDTO);
        }

        @Test
        void shouldReturnProperUserId() {
            assertThat(user.getUserId()).isEqualTo(userId);
        }

        @Test
        void shouldReturnProperName() {
            assertThat(user.getName()).isEqualTo(andrzejNowakDTO.getName());
        }

        @Test
        void shouldReturnProperSurname() {
            assertThat(user.getSurname()).isEqualTo(andrzejNowakDTO.getSurname());
        }

        @Test
        void shouldReturnProperEmail() {
            assertThat(user.getEmail()).isEqualTo(andrzejNowakDTO.getEmail());
        }

        @Test
        void shouldReturnProperPhoneNumber() {
            assertThat(user.getPhoneNumber()).isEqualTo(andrzejNowakDTO.getPhoneNumber());
        }
    }
}
