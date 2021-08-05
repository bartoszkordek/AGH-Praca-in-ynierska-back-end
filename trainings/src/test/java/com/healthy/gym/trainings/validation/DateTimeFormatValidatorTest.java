package com.healthy.gym.trainings.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeFormatValidatorTest {
    private DateTimeFormatValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DateTimeFormatValidator();
        context = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-10-10T22:00",
            "2021-10-11T12:00:01",
            "2021-12-12T00:00:01",
            "2021-12-12T00:10",
    })
    void shouldBeTrue(String validID) {
        assertThat(
                validator.isValid(validID, context)
        ).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-10-10 22:00",
            "2021-20-12T12:00",
            "2021-12-12 T 00:00:01",
            "21-12-12T00:10",
    })
    void shouldBeFalse(String validID) {
        assertThat(
                validator.isValid(validID, context)
        ).isFalse();
    }
}