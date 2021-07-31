package com.healthy.gym.trainings.utils;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.healthy.gym.trainings.utils.GroupTrainingValidator.isHallNoInvalid;
import static com.healthy.gym.trainings.utils.GroupTrainingValidator.isStartTimeAfterEndTime;
import static org.assertj.core.api.Assertions.assertThat;

class GroupTrainingValidatorTest {

    @Nested
    class WhenIsHallNoInvalid {
        @Test
        void shouldReturnFalseWhenHallNoIs5() {
            assertThat(isHallNoInvalid(5)).isFalse();
        }

        @Test
        void shouldReturnTrueWhenHallNoIs0() {
            assertThat(isHallNoInvalid(0)).isTrue();
        }
    }

    @Nested
    class WhenIsStartTimeAfterEndTime {
        @Test
        void shouldReturnFalseWhenStartDateIsBeforeEndTime() {
            assertThat(isStartTimeAfterEndTime("10:00", "10:01")).isFalse();
        }

        @Test
        void shouldReturnTrueWhenStartDateIsAfterEndTime() {
            assertThat(isStartTimeAfterEndTime("10:20", "10:01")).isTrue();
        }
    }
}