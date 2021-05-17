package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.data.repository.UserDAO;
import com.healthy.gym.auth.service.UserService;
import com.healthy.gym.auth.shared.UserDTO;
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
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceWhenGetUserDetailsByEmailIsCalledTest {

    private UserEntity janKowalskiEntity;
    private UserDTO user;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

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

        when(userDAO.findByEmail(janKowalskiEntity.getEmail()))
                .thenReturn(janKowalskiEntity);

        user = userService.getUserDetailsByEmail("jan.kowalski@test.com");
    }

    @Test
    void shouldThrowException() {
        assertThatThrownBy(
                () -> userService.getUserDetailsByEmail("invalidEmail@test.com")
        ).isInstanceOf(UsernameNotFoundException.class);
    }

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
