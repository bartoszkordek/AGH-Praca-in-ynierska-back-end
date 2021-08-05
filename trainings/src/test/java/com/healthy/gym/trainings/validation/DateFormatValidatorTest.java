package com.healthy.gym.trainings.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

class DateFormatValidatorTest {
    private DateFormatValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DateFormatValidator();
        context = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-10-10",
            "2021-01-11",
            "1999-12-12",
            "2000-11-12",
    })
    void shouldBeTrue(String value) {
        assertThat(
                validator.isValid(value, context)
        ).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-10-10 22:00",
            "2021-20-12T12:00",
            "2021-12-12 T 00:00:01",
            "21-12-12T00:10",
            "2020-1010",
            "20210111",
            "1999.12.12",
            "2000-14-12",
    })
    void shouldBeFalse(String value) {
        assertThat(
                validator.isValid(value, context)
        ).isFalse();
    }
}