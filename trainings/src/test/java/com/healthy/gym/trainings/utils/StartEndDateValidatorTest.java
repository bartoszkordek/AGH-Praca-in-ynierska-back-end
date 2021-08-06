package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.exception.StartDateAfterEndDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StartEndDateValidatorTest {

    private StartEndDateValidator validator;
    private LocalDateTime beginningOfStartDate;
    private LocalDateTime endOfEndDate;

    @BeforeEach
    void setUp() throws StartDateAfterEndDateException {
        validator = new StartEndDateValidator("2020-10-01", "2020-10-03");

    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        assertThatThrownBy(
                () -> new StartEndDateValidator("2020-10-02", "2020-10-01")
        ).isInstanceOf(StartDateAfterEndDateException.class);
    }

    @Nested
    class WhenGetBeginningOfStartDate {

        @BeforeEach
        void setUp() {
            beginningOfStartDate = validator.getBeginningOfStartDate();
        }

        @Test
        void shouldGetProperYear() {
            assertThat(beginningOfStartDate.getYear()).isEqualTo(2020);
        }

        @Test
        void shouldGetProperMonth() {
            assertThat(beginningOfStartDate.getMonthValue()).isEqualTo(10);
        }

        @Test
        void shouldGetProperDay() {
            assertThat(beginningOfStartDate.getDayOfMonth()).isEqualTo(1);
        }

        @Test
        void shouldGetProperHour() {
            assertThat(beginningOfStartDate.getHour()).isZero();
        }

        @Test
        void shouldGetProperMinutes() {
            assertThat(beginningOfStartDate.getMinute()).isZero();
        }

        @Test
        void shouldGetProperSeconds() {
            assertThat(beginningOfStartDate.getSecond()).isZero();
        }
    }

    @Nested
    class WhenGetEndOfEndDate {

        @BeforeEach
        void setUp() {
            endOfEndDate = validator.getEndOfEndDate();
        }

        @Test
        void shouldGetProperYear() {
            assertThat(endOfEndDate.getYear()).isEqualTo(2020);
        }

        @Test
        void shouldGetProperMonth() {
            assertThat(endOfEndDate.getMonthValue()).isEqualTo(10);
        }

        @Test
        void shouldGetProperDay() {
            assertThat(endOfEndDate.getDayOfMonth()).isEqualTo(3);
        }

        @Test
        void shouldGetProperHour() {
            assertThat(endOfEndDate.getHour()).isEqualTo(23);
        }

        @Test
        void shouldGetProperMinutes() {
            assertThat(endOfEndDate.getMinute()).isEqualTo(59);
        }

        @Test
        void shouldGetProperSeconds() {
            assertThat(endOfEndDate.getSecond()).isEqualTo(59);
        }
    }
}