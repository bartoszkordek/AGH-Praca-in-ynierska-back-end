package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.data.repository.UserDAO;
import com.healthy.gym.auth.service.UserService;
import com.healthy.gym.auth.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceWhenCreateUserIsCalledTest {

    private UserDTO savedUserDTO;
    private UserDTO andrzejNowakDTO;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

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
