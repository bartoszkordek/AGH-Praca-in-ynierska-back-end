package com.healthy.gym.auth.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationTokenDTOTest {

    private RegistrationTokenDTO token1;
    private RegistrationTokenDTO token2;

    @BeforeEach
    void setUp() {
        token1 = new RegistrationTokenDTO();
        token2 = new RegistrationTokenDTO();
    }

    @Test
    void twoEmptyRegistrationTokenDTOShouldBeEqual() {
        assertThat(token1)
                .isEqualTo(token2)
                .hasSameHashCodeAs(token2);
    }

    @Test
    void registrationTokenDTOHasAllNullFields() {
        assertThat(token1).hasAllNullFieldsOrPropertiesExcept("wasUsed");
        assertThat(token2).hasAllNullFieldsOrPropertiesExcept("wasUsed");
    }

    @Test
    void twoRegistrationTokenDTOWithSameFieldsShouldBeEqual() {
        String token = UUID.randomUUID().toString();
        token1.setToken(token);
        token2.setToken(token);
        assertThat(token1).isEqualTo(token2).hasSameHashCodeAs(token2);
    }

    @Test
    void twoRegistrationTokenDTOWithDifferentFieldsShouldNotBeEqual() {
        token1.setToken(UUID.randomUUID().toString());
        assertThat(token1).isNotEqualTo(token2);
    }
}