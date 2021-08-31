package com.healthy.gym.account.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RolesValidatorTest {

    private RolesValidator validator;

    private static Stream<Arguments> provideValidRoleList() {
        return Stream.of(
                Arguments.of(List.of("user", "trainer")),
                Arguments.of(List.of("user", "trainer", "manager")),
                Arguments.of(List.of("user", "admin")),
                Arguments.of(List.of("user", "employee")),
                Arguments.of(List.of("uSer", " EmployeE  "))
        );
    }

    private static Stream<Arguments> provideInvalidRoleList() {
        return Stream.of(
                Arguments.of(List.of("tra1ner")),
                Arguments.of(List.of("use r ", "trainer", "manager")),
                Arguments.of(List.of("uwser", "admin")),
                Arguments.of(List.of("user", "eqmployee")),
                Arguments.of(List.of())
        );
    }

    @BeforeEach
    void setUp() {
        validator = new RolesValidator();
    }

    @ParameterizedTest
    @MethodSource("provideValidRoleList")
    void shouldReturnTrueWhenAllValidRoles(List<String> roles) {
        boolean isValid = validator.isValid(roles, null);
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRoleList")
    void shouldReturnFalseWhenAtLeastOneInvalid(List<String> roles) {
        boolean isValid = validator.isValid(roles, null);
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalse() {
        boolean isValid = validator.isValid(null, null);
        assertThat(isValid).isFalse();
    }
}