package com.healthy.gym.auth.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator phoneNumberValidator;

    @BeforeEach
    void setUp() {
        phoneNumberValidator = new PhoneNumberValidator();
    }

    @Test
    void givenBlankNumberShouldMatchRegexPatter() {
        assertTrue(phoneNumberValidator.isValid("     ", null));
    }

    @Test
    void givenNullShouldMatchRegexPatter() {
        assertTrue(phoneNumberValidator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+48123465876",
            "123465876",
            "+48 123 465 876",
            "+48-123-465-876",
            "123-465-876",
            "123 465 876",
    })
    void givenNumbersShouldMatchRegexPattern(String phoneNumber) {
        assertTrue(phoneNumberValidator.isValid(phoneNumber, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678",
            "+48123-465-876",
            "123 465-876",
            "+48-123 465-876",
            "+48-123-465 876",
            "+48 123-465-876",
            "+48-123 65-876",
            "+48-123465876",
            "adasf",
            "+48123465876 ",
            "123465876 1",
            "+48 123 465 876 1",
            "+48-123-465-876 ",
            "+48 123465876",
            "@#r ada"
    })
    void givenNumbersShouldNotMatchRegexPattern(String phoneNumber) {
        assertFalse(phoneNumberValidator.isValid(phoneNumber, null));
    }
}