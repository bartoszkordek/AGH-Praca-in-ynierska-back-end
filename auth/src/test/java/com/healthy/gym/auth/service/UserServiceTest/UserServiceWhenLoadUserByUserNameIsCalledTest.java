package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.entity.UserEntity;
import com.healthy.gym.auth.data.repository.UserDAO;
import com.healthy.gym.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceWhenLoadUserByUserNameIsCalledTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private UserDAO userDAO;

    private UserEntity testEntity;
    private UserDetails user;

    @BeforeEach
    void setUp() {
        testEntity = new UserEntity();
        testEntity.setEmail("test@test.com");
        testEntity.setEncryptedPassword("password");
        when(userDAO.findByEmail(anyString())).thenReturn(testEntity);
    }

    @Test
    void shouldReturnEmailAsUserName() {
        user = userService.loadUserByUsername(anyString());
        assertThat(user.getUsername()).isEqualTo("test@test.com");
    }

    @Test
    void shouldReturnPassword() {
        user = userService.loadUserByUsername(anyString());
        assertThat(user.getPassword()).isEqualTo("password");
    }

    @Test
    void shouldReturnEnabledUser() {
        testEntity.setEnabled(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldReturnAccountNonExpiredSetToTrue() {
        testEntity.setAccountNonExpired(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void shouldReturnCredentialsNonExpiredSetToTrue() {
        testEntity.setCredentialsNonExpired(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldReturnAccountNonLockedSetToTrue() {
        testEntity.setAccountNonLocked(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    void shouldReturnEmptyAuthorities() {
        user = userService.loadUserByUsername(anyString());
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void shouldThrowUsernameNotFoundException() {
        doThrow(UsernameNotFoundException.class).when(userDAO).findByEmail(anyString());
        assertThatThrownBy(
                () -> userService.loadUserByUsername(anyString())
        ).isInstanceOf(UsernameNotFoundException.class);
    }
}
