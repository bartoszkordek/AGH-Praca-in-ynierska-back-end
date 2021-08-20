package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.document.UserPrivacyDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.data.repository.UserPrivacyDAO;
import com.healthy.gym.account.dto.UserPrivacyDTO;
import com.healthy.gym.account.exception.UserPrivacyNotUpdatedException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WhenChangeUserPrivacyTest {

    private AccountService accountService;
    private UserDAO userDAO;
    private UserPrivacyDAO userPrivacyDAO;
    private UserDocument andrzejNowak;
    private UserPrivacyDocument userPrivacyDocument;
    private String userId;
    private UserPrivacyDTO userPrivacyDTOToChange;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                "encryptedPassword4576",
                userId
        );

        userPrivacyDocument = new UserPrivacyDocument(
                true,
                true,
                true,
                true,
                andrzejNowak
        );

        userPrivacyDTOToChange = new UserPrivacyDTO(
                true,
                true,
                true,
                true
        );

        userDAO = mock(UserDAO.class);
        userPrivacyDAO = mock(UserPrivacyDAO.class);
        accountService = new AccountServiceImpl(userDAO, userPrivacyDAO, null);
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.changeUserPrivacy(new UserPrivacyDTO(), userId)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUserPrivacyNotUpdated() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        when(userPrivacyDAO.findByUserDocument(andrzejNowak)).thenReturn(new UserPrivacyDocument());
        when(userPrivacyDAO.save(any())).thenReturn(new UserPrivacyDocument());

        assertThatThrownBy(
                () -> accountService.changeUserPrivacy(userPrivacyDTOToChange, userId)
        ).isInstanceOf(UserPrivacyNotUpdatedException.class);
    }

    @Nested
    class ShouldSavePrivacyDocumentWhenUserPrivacyNotFound {
        private UserPrivacyDTO userPrivacyDTO;

        @BeforeEach
        void setUp() throws UserPrivacyNotUpdatedException {
            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            when(userPrivacyDAO.findByUserDocument(andrzejNowak)).thenReturn(null);
            when(userPrivacyDAO.save(any())).thenReturn(userPrivacyDocument);
            userPrivacyDTO = accountService.changeUserPrivacy(userPrivacyDTOToChange, userId);
        }

        @Test
        void shouldRegulationAcceptedBeTrue() {
            assertThat(userPrivacyDTO.isRegulationsAccepted()).isTrue();
        }

        @Test
        void shouldAllowShowingAvatarBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingAvatar()).isTrue();
        }

        @Test
        void shouldAllowShowingUserStatisticsBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingUserStatistics()).isTrue();
        }

        @Test
        void shouldAllowShowingTrainingsParticipationBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingTrainingsParticipation()).isTrue();
        }
    }

    @Nested
    class ShouldSavePrivacyDocumentWhenUserPrivacyFound {
        private UserPrivacyDTO userPrivacyDTO;

        @BeforeEach
        void setUp() throws UserPrivacyNotUpdatedException {
            UserPrivacyDocument privacyDocument = new UserPrivacyDocument(
                    true,
                    false,
                    true,
                    false,
                    andrzejNowak
            );

            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            when(userPrivacyDAO.findByUserDocument(andrzejNowak)).thenReturn(privacyDocument);
            when(userPrivacyDAO.save(any())).thenReturn(userPrivacyDocument);

            userPrivacyDTO = accountService.changeUserPrivacy(userPrivacyDTOToChange, userId);
        }

        @Test
        void shouldRegulationAcceptedBeTrue() {
            assertThat(userPrivacyDTO.isRegulationsAccepted()).isTrue();
        }

        @Test
        void shouldAllowShowingAvatarBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingAvatar()).isTrue();
        }

        @Test
        void shouldAllowShowingUserStatisticsBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingUserStatistics()).isTrue();
        }

        @Test
        void shouldAllowShowingTrainingsParticipationBeTrue() {
            assertThat(userPrivacyDTO.isAllowShowingTrainingsParticipation()).isTrue();
        }
    }
}
