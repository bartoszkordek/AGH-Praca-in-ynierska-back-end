package com.healthy.gym.auth.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResetPasswordTokenDTOTest {
    private ResetPasswordTokenDTO token1;
    private ResetPasswordTokenDTO token2;

    @BeforeEach
    void setUp() {
        token1 = new ResetPasswordTokenDTO();
        token2 = new ResetPasswordTokenDTO();
    }

    @Test
    void twoEmptyResetPasswordTokenDTOShouldBeEqual() {
        assertThat(token1)
                .isEqualTo(token2)
                .hasSameHashCodeAs(token2);
    }

    @Test
    void resetPasswordTokenDTOHasAllNullFields() {
        assertThat(token1).hasAllNullFieldsOrPropertiesExcept("wasUsed");
        assertThat(token2).hasAllNullFieldsOrPropertiesExcept("wasUsed");
    }

    @Test
    void twoResetPasswordTokenDTOWithSameFieldsShouldBeEqual() {
        String token = UUID.randomUUID().toString();
        token1.setToken(token);
        token2.setToken(token);
        assertThat(token1).isEqualTo(token2).hasSameHashCodeAs(token2);
    }

    @Test
    void twoResetPasswordTokenDTOWithDifferentFieldsShouldNotBeEqual() {
        token1.setToken(UUID.randomUUID().toString());
        assertThat(token1).isNotEqualTo(token2);
    }
}