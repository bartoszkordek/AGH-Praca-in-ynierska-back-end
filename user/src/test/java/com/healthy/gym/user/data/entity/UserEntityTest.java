package com.healthy.gym.user.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {
    private UserEntity userEntity1;
    private UserEntity userEntity2;

    @BeforeEach
    void setUp() {
        userEntity1 = new UserEntity();
        userEntity2 = new UserEntity();
    }

    @Test
    void twoObjectsOfEmptyUserEntityShouldBeEqual() {
        assertThat(userEntity1)
                .isEqualTo(userEntity2)
                .hasSameHashCodeAs(userEntity2);
    }

    @Test
    void twoDifferentObjectsOfUserEntityShouldNotBeEqual() {
        userEntity1.setId(1L);
        userEntity1.setName("Jan");
        userEntity1.setSurname("Kowalski");
        userEntity1.setEmail("jan.kowalski@test.com");
        userEntity1.setPhoneNumber("666 777 888");

        userEntity2.setId(2L);
        userEntity2.setName("Janina");
        userEntity2.setSurname("Kowalska");
        userEntity2.setEmail("janina.kowalska@test.com");
        userEntity2.setPhoneNumber("666 777 888");

        assertThat(userEntity1).isNotEqualTo(userEntity2);
        assertThat(userEntity1.hashCode()).isNotEqualTo(userEntity2.hashCode());
    }

    @Test
    void twoObjectsOfUserEntityWithSameFieldValuesShouldBeEqual() {
        userEntity1.setId(1L);
        userEntity1.setName("Jan");
        userEntity1.setSurname("Kowalski");
        userEntity1.setEmail("jan.kowalski@test.com");
        userEntity1.setPhoneNumber("666 777 888");

        userEntity2.setId(1L);
        userEntity2.setName("Jan");
        userEntity2.setSurname("Kowalski");
        userEntity2.setEmail("jan.kowalski@test.com");
        userEntity2.setPhoneNumber("666 777 888");

        assertThat(userEntity1)
                .isEqualTo(userEntity2)
                .hasSameHashCodeAs(userEntity2);
    }
}