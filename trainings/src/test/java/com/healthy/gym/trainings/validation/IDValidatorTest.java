package com.healthy.gym.trainings.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

class IDValidatorTest {
    private IDValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new IDValidator();
        context = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "e1176ebb-da30-4193-b974-68b3baa60351",
            "aa854438-1122-40c1-b01e-dc44d6633e43",
            "103f0f36-b3e9-4c72-b1d5-c963d02dc2ea",
            "b03e76f6-4cdb-4629-a56a-a7f5b4bac213"
    })
    void shouldBeTrue(String validID) {
        assertThat(
                validator.isValid(validID, context)
        ).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "e1176ebt-da30-4193-b974-68b3baa60351",
            "aa854438.1122-40c1-b01e-dc44d6633e43",
            "das",
            "1231"
    })
    void shouldBeFalse(String validID) {
        assertThat(
                validator.isValid(validID, context)
        ).isFalse();
    }
}