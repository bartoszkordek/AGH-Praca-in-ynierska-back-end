package com.healthy.gym.user.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void twoObjectsOfEmptyUserEntityShouldBeEqual() {
        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();

        assertThat(userEntity1)
                .isEqualTo(userEntity2)
                .hasSameHashCodeAs(userEntity2);
    }

    @Test
    void twoDifferentObjectsOfUserEntityShouldNotBeEqual() {
        UserEntity janKowalskiEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                bCryptPasswordEncoder.encode("password1234"),
                "666 777 888",
                UUID.randomUUID().toString(),
                false
        );

        UserEntity mariaNowakEntity = new UserEntity(
                "Maria",
                "Nowak",
                "maria.nowak@test.com",
                bCryptPasswordEncoder.encode("password3456"),
                "686 777 888",
                UUID.randomUUID().toString(),
                false
        );

        assertThat(janKowalskiEntity).isNotEqualTo(mariaNowakEntity);
        assertThat(janKowalskiEntity.hashCode()).isNotEqualTo(mariaNowakEntity.hashCode());
    }

    @Test
    void twoObjectsOfUserEntityWithSameFieldValuesShouldBeEqual() {
        String userID = UUID.randomUUID().toString();
        String password = bCryptPasswordEncoder.encode("password1234");

        UserEntity janKowalskiEntity1 = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                password,
                userID,
                true
        );

        UserEntity janKowalskiEntity2 = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                password,
                userID,
                true
        );

        assertThat(janKowalskiEntity1)
                .isEqualTo(janKowalskiEntity2)
                .hasSameHashCodeAs(janKowalskiEntity2);
    }
}