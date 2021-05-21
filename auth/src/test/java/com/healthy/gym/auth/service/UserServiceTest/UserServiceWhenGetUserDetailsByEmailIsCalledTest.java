package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
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

    private UserDocument janKowalski;
    private UserDTO user;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

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

        when(userDAO.findByEmail(janKowalski.getEmail()))
                .thenReturn(janKowalski);

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
        assertThat(user.getName()).isEqualTo(janKowalski.getName());
    }

    @Test
    void shouldReturnUserDTOWithProperSurname() {
        assertThat(user.getSurname()).isEqualTo(janKowalski.getSurname());
    }

    @Test
    void shouldReturnUserDTOWithProperEmail() {
        assertThat(user.getEmail()).isEqualTo(janKowalski.getEmail());
    }

    @Test
    void shouldReturnUserDTOWithProperPhoneNumber() {
        assertThat(user.getPhoneNumber()).isEqualTo(janKowalski.getPhoneNumber());
    }

    @Test
    void shouldReturnUserDTOWithProperPassword() {
        assertThat(user.getEncryptedPassword()).isEqualTo(janKowalski.getEncryptedPassword());
    }

    @Test
    void shouldReturnUserDTOWithProperPassword2() {
        assertThat(user.getPassword()).isNull();
    }

    @Test
    void shouldReturnUserDTOWithProperUserId() {
        assertThat(user.getUserId()).isEqualTo(janKowalski.getUserId());
    }
}
