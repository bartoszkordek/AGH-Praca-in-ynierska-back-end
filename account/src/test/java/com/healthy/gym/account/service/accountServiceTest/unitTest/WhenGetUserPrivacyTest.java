package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.document.UserPrivacyDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.data.repository.UserPrivacyDAO;
import com.healthy.gym.account.dto.UserPrivacyDTO;
import com.healthy.gym.account.exception.UserPrivacyNotFoundException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenGetUserPrivacyTest {

    private AccountService accountService;
    private UserDAO userDAO;
    private UserPrivacyDAO userPrivacyDAO;
    private UserPrivacyDocument userPrivacyDocument;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userPrivacyDocument = new UserPrivacyDocument(
                true,
                true,
                true,
                true,
                new UserDocument()
        );

        userDAO = mock(UserDAO.class);
        userPrivacyDAO = mock(UserPrivacyDAO.class);
        accountService = new AccountServiceImpl(userDAO, userPrivacyDAO, null);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.getUserPrivacy(userId)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserPrivacyIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(userPrivacyDAO.findByUserDocument(any())).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.getUserPrivacy(userId)
        ).isInstanceOf(UserPrivacyNotFoundException.class);
    }

    @Test
    void shouldReturnUserPrivacyData() throws UserPrivacyNotFoundException {
        when(userDAO.findByUserId(userId)).thenReturn(new UserDocument());
        when(userPrivacyDAO.findByUserDocument(any())).thenReturn(userPrivacyDocument);
        UserPrivacyDTO returnedDTO = accountService.getUserPrivacy(userId);

        assertThat(returnedDTO.isRegulationsAccepted()).isTrue();
        assertThat(returnedDTO.isAllowShowingTrainingsParticipation()).isTrue();
        assertThat(returnedDTO.isAllowShowingUserStatistics()).isTrue();
        assertThat(returnedDTO.isAllowShowingAvatar()).isTrue();
    }
}
