package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserDAOTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserDAO userDAO;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setName("John");
        userEntity.setSurname("Smith");
        userEntity.setEmail("john.smith@gmail.com");
        userEntity.setEncryptedPassword("password");
        userEntity.setPhoneNumber("546 324 324");

        testEntityManager.persist(userEntity);
        testEntityManager.flush();
    }

    @Test
    void shouldReturnUserWhenFindByName() {
        String email = "john.smith@gmail.com";
        UserEntity responseEntity = userDAO.findByEmail(email);
        assertThat(responseEntity.getEmail()).isEqualTo(email);
    }

    @Test
    void shouldNotReturnUserWhenFindByName() {
        String email = "john1.smith@gmail.com";
        UserEntity responseEntity = userDAO.findByEmail(email);
        assertThat(responseEntity).isNull();
    }
}