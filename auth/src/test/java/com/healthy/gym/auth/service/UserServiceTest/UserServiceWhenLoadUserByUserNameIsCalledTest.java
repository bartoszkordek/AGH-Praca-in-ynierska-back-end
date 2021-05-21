package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
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

    private UserDocument testDocument;
    private UserDetails user;

    @BeforeEach
    void setUp() {
        testDocument = new UserDocument();
        testDocument.setEmail("test@test.com");
        testDocument.setEncryptedPassword("password");
        when(userDAO.findByEmail(anyString())).thenReturn(testDocument);
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
        testDocument.setEnabled(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void shouldReturnAccountNonExpiredSetToTrue() {
        testDocument.setAccountNonExpired(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isAccountNonExpired()).isTrue();
    }

    @Test
    void shouldReturnCredentialsNonExpiredSetToTrue() {
        testDocument.setCredentialsNonExpired(true);
        user = userService.loadUserByUsername(anyString());
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldReturnAccountNonLockedSetToTrue() {
        testDocument.setAccountNonLocked(true);
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
