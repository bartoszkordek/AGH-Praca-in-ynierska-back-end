package com.healthy.gym.auth.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

class EmailValidatorTest {

    private EmailValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new EmailValidator();
        context = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "xmr09697@zwohocom",
            "xmr09697zwoho.com",
            "xmr09697@zwoho.com    ",
            "     xmr09697@zwoho.com",
            "     xmr09697@zwoho.com   ",
            "",
            "  ",
            "xmr09697@@zwoho.com",
            "xmr09697@zwoho.com!",
            "John..Doe@example.com",
    })
    void shouldBeFalse(String invalidEmail) {
        assertThat(
                validator.isValid(invalidEmail, context)
        ).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "xmr09697@zwoho.com",
            "jan.kowalski@wp.pl",
            "\"John..Doe\"@example.com"
    })
    void shouldBeTrue(String validEmail) {
        assertThat(
                validator.isValid(validEmail, context)
        ).isTrue();
    }
}