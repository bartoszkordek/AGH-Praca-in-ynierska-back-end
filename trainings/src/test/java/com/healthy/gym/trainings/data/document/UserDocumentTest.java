package com.healthy.gym.trainings.data.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDocumentTest {
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void twoObjectsOfEmptyUserDocumentShouldBeEqual() {
        UserDocument userDocument1 = new UserDocument();
        UserDocument userDocument2 = new UserDocument();

        assertThat(userDocument2)
                .isEqualTo(userDocument1)
                .hasSameHashCodeAs(userDocument1);
    }

    @Test
    void twoDifferentObjectsOfUserDocumentShouldNotBeEqual() {
        UserDocument janKowalski = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString()
        );

        UserDocument mariaNowak = new UserDocument(
                "Maria",
                "Nowak",
                "maria.nowak@test.com",
                "686 777 888",
                bCryptPasswordEncoder.encode("password3456"),
                UUID.randomUUID().toString()
        );

        assertThat(janKowalski).isNotEqualTo(mariaNowak);
        assertThat(janKowalski.hashCode()).isNotEqualTo(mariaNowak.hashCode());
    }

    @Test
    void twoObjectsOfUserDocumentWithSameFieldValuesShouldBeEqual() {
        String userID = UUID.randomUUID().toString();
        String password = bCryptPasswordEncoder.encode("password1234");

        UserDocument janKowalski1 = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                password,
                userID
        );

        UserDocument janKowalski2 = new UserDocument(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                password,
                userID
        );

        assertThat(janKowalski1)
                .isEqualTo(janKowalski2)
                .hasSameHashCodeAs(janKowalski2);
    }

    @Test
    void whenCreatedHaveAllFieldNullExceptBooleanLong() {
        assertThat(new UserDocument()).hasAllNullFieldsOrPropertiesExcept(
                "id", "enabled", "accountNonExpired", "credentialsNonExpired", "accountNonLocked"
        );
    }
}