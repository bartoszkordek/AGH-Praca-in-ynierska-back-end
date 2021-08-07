package com.healthy.gym.trainings.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PageValidatorTest {
    private PageValidator pageValidator;

    @BeforeEach
    void setUp() {
        pageValidator = new PageValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "10", "20", "50", "100", "100000", "2147483647"})
    void shouldReturnTrue(String value) {
        assertThat(pageValidator.isValid(value, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "asdas", "$332", "-100000", "-2147483648"})
    void shouldReturnFalse(String value) {
        assertThat(pageValidator.isValid(value, null)).isFalse();
    }
}