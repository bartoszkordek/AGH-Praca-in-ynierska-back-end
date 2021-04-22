package com.healthy.gym.user;

import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.controller.UserController;
import com.healthy.gym.user.data.repository.UserDAO;
import com.healthy.gym.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserController userController;

    @Test
    @WithMockUser
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    @Test
    void shouldReturnComponentsInTheCurrentScope(){
        assertThat(applicationContext.getBean(UserController.class)).isNotNull();
        assertThat(applicationContext.getBean(Translator.class)).isNotNull();
        assertThat(applicationContext.getBean(UserService.class)).isNotNull();
        assertThat(applicationContext.getBean(UserDAO.class)).isNotNull();
        assertThat(applicationContext.getBean(BCryptPasswordEncoder.class)).isNotNull();
    }

}
