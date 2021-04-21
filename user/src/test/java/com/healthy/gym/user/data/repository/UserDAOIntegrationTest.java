package com.healthy.gym.user.data.repository;

import com.healthy.gym.user.data.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserDAOIntegrationTest {

    private UserEntity janKowalskiEntity, mariaNowakEntity, andrzejNowakEntity;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        janKowalskiEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                bCryptPasswordEncoder.encode("password1234"),
                "666 777 888",
                UUID.randomUUID().toString()
        );

        mariaNowakEntity = new UserEntity(
                "Maria",
                "Nowak",
                "maria.nowak@test.com",
                bCryptPasswordEncoder.encode("password3456"),
                "686 777 888",
                UUID.randomUUID().toString()
        );

        andrzejNowakEntity = new UserEntity(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                bCryptPasswordEncoder.encode("password4576"),
                "676 777 888",
                UUID.randomUUID().toString()
        );

        testEntityManager.persist(janKowalskiEntity);
        testEntityManager.persist(mariaNowakEntity);
        testEntityManager.persist(andrzejNowakEntity);
        testEntityManager.flush();
    }

    @Test
    void shouldReturnProperUserEntityWhenFindByEmailIsCalled() {
        UserEntity found = userDAO.findByEmail("jan.kowalski@test.com");
        assertThat(found)
                .isEqualTo(janKowalskiEntity)
                .hasSameHashCodeAs(janKowalskiEntity);
    }

    @Test
    void shouldReturnNullForIfUserNonExists() {
        UserEntity found = userDAO.findByEmail("non.existing@test.com");
        assertThat(found).isNull();
    }

    @Test
    void shouldReturnAllUsersInDatabase() {
        List<UserEntity> found = (List<UserEntity>) userDAO.findAll();
        System.out.println(found);
        assertThat(found)
                .isNotNull()
                .hasSize(3)
                .extracting(UserEntity::getUserId)
                .contains(
                        andrzejNowakEntity.getUserId(),
                        mariaNowakEntity.getUserId(),
                        janKowalskiEntity.getUserId()
                );
    }
}