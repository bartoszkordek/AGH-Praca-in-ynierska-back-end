package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
import com.healthy.gym.user.data.repository.UserDAO;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

    @MockBean
    private RegistrationTokenDAO registrationTokenDAO;

    @Nested
    class WhenGetUserDetailsByEmailIsCalled {
        private UserEntity janKowalskiEntity;
        private UserDTO user;

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

            UserEntity andrzejNowakEntity = new UserEntity(
                    "Andrzej",
                    "Nowak",
                    "andrzej.nowak@test.com",
                    "676 777 888",
                    bCryptPasswordEncoder.encode("password4576"),
                    UUID.randomUUID().toString(),
                    true
            );

            when(userDAO.findByEmail(janKowalskiEntity.getEmail()))
                    .thenReturn(janKowalskiEntity);

            user = userService.getUserDetailsByEmail("jan.kowalski@test.com");
        }

        @Test
        void shouldThrowExceptionWhenInvalidEmailIsProvided() {
            assertThatThrownBy(
                    () -> userService.getUserDetailsByEmail("invalidEmail@test.com")
            ).isInstanceOf(UsernameNotFoundException.class);
        }

        @Nested
        class WhenValidEmailProvided {
            @Test
            void shouldReturnUserDTOWithProperName() {
                assertThat(user.getName()).isEqualTo(janKowalskiEntity.getName());
            }

            @Test
            void shouldReturnUserDTOWithProperSurname() {
                assertThat(user.getSurname()).isEqualTo(janKowalskiEntity.getSurname());
            }

            @Test
            void shouldReturnUserDTOWithProperEmail() {
                assertThat(user.getEmail()).isEqualTo(janKowalskiEntity.getEmail());
            }

            @Test
            void shouldReturnUserDTOWithProperPhoneNumber() {
                assertThat(user.getPhoneNumber()).isEqualTo(janKowalskiEntity.getPhoneNumber());
            }

            @Test
            void shouldReturnUserDTOWithProperPassword() {
                assertThat(user.getEncryptedPassword()).isEqualTo(janKowalskiEntity.getEncryptedPassword());
            }

            @Test
            void shouldReturnUserDTOWithProperPassword2() {
                assertThat(user.getPassword()).isNull();
            }

            @Test
            void shouldReturnUserDTOWithProperUserId() {
                assertThat(user.getUserId()).isEqualTo(janKowalskiEntity.getUserId());
            }
        }
    }

    @Nested
    class WhenCreateUserIsCalled {
        private UserDTO savedUserDTO;
        private UserDTO andrzejNowakDTO;

        @BeforeEach
        void setUp() {
            andrzejNowakDTO = new UserDTO(
                    null,
                    "Andrzej",
                    "Nowak",
                    "andrzej.nowak@test.com",
                    "676 777 888",
                    "password4576",
                    null
            );

            UserEntity andrzejNowakEntity = new UserEntity(
                    "Andrzej",
                    "Nowak",
                    "andrzej.nowak@test.com",
                    "676 777 888",
                    bCryptPasswordEncoder.encode("password4576"),
                    UUID.randomUUID().toString(),
                    true
            );

            when(userDAO.save(Mockito.any(UserEntity.class)))
                    .thenReturn(andrzejNowakEntity);

            savedUserDTO = userService.createUser(andrzejNowakDTO);
        }

        @Test
        void shouldReturnUserDTOWithUserID() {
            assertThat(savedUserDTO.getUserId())
                    .isNotNull()
                    .isNotEmpty()
                    .isInstanceOf(String.class)
                    .hasSize(36)
                    .matches(uuid -> {
                        try {
                            UUID.fromString(uuid);
                        } catch (IllegalArgumentException e) {
                            return false;
                        }
                        return true;
                    });
        }

        @Test
        void shouldReturnUserDTOWithProperName() {
            assertThat(savedUserDTO.getName()).isEqualTo(andrzejNowakDTO.getName());
        }

        @Test
        void shouldReturnUserDTOWithProperSurname() {
            assertThat(savedUserDTO.getSurname()).isEqualTo(andrzejNowakDTO.getSurname());
        }

        @Test
        void shouldReturnUserDTOWithProperEmail() {
            assertThat(savedUserDTO.getEmail()).isEqualTo(andrzejNowakDTO.getEmail());
        }

        @Test
        void shouldReturnUserDTOWithProperPhoneNumber() {
            assertThat(savedUserDTO.getPhoneNumber()).isEqualTo(andrzejNowakDTO.getPhoneNumber());
        }

        @Test
        void shouldNotReturnUserDTOWithPlainTextPassword() {
            assertThat(savedUserDTO.getPassword()).isNull();
        }

        @Test
        void shouldReturnUserDTOWithMatchingEncodedPassword() {
            assertThat(savedUserDTO.getEncryptedPassword())
                    .isNotNull()
                    .isNotEmpty()
                    .isInstanceOf(String.class)
                    .matches(password -> bCryptPasswordEncoder.matches(andrzejNowakDTO.getPassword(), password));
        }
    }

    @Nested
    class WhenCreateRegistrationTokenIsCalled {

        private String token;
        private UserDTO userDTO;
        private RegistrationToken registrationToken;
        private UserEntity janKowalskiEntity;

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

            when(registrationTokenDAO.save(any(RegistrationToken.class)))
                    .thenReturn(new RegistrationToken(token, janKowalskiEntity));

            registrationToken = userService.createRegistrationToken(userDTO, token);
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
    class WhenVerifyRegistrationTokenIsCalled {

        @Test
        void shouldThrowInvalidTokenExceptionWhenProvidedInvalidToken() {
            when(registrationTokenDAO.findByToken(anyString())).thenReturn(null);
            assertThatThrownBy(
                    () -> userService.verifyRegistrationToken(anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            RegistrationToken expiredToken = new RegistrationToken();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> userService.verifyRegistrationToken(anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithRegisterToken() {
            RegistrationToken registrationToken = spy(RegistrationToken.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            when(registrationToken.getUserEntity()).thenReturn(null);

            assertThatThrownBy(
                    () -> userService.verifyRegistrationToken(anyString())
            ).isInstanceOf(IllegalStateException.class);
        }
    }
}