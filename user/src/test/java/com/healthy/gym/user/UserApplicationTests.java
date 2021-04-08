package com.healthy.gym.user;

import com.healthy.gym.user.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class UserApplicationTests {

    @Autowired
    private UserController userController;

    @Test
    @WithMockUser
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

}
