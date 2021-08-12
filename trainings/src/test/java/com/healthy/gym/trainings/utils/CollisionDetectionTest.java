package com.healthy.gym.trainings.utils;

import com.healthy.gym.trainings.utils.test.data.ModifiedBinarySearchTestData;
import com.healthy.gym.trainings.utils.test.data.ShouldNotOverlapWithAnyTestData;
import com.healthy.gym.trainings.utils.test.data.ShouldOverlapWithAnyTestData;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.healthy.gym.trainings.utils.CollisionDetection.modifiedBinarySearchGetPointersToNearestNumbers;
import static com.healthy.gym.trainings.utils.CollisionDetection.overlapsWithAny;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollisionDetectionTest {

    @ParameterizedTest
    @ArgumentsSource(ModifiedBinarySearchTestData.class)
    void binarySearchNearestNumbersShouldReturnProperPointers(
            long checkedNumber,
            long[][] comparativeInterval,
            int[] expectedPointers
    ) {
        assertThat(modifiedBinarySearchGetPointersToNearestNumbers(checkedNumber, comparativeInterval))
                .isEqualTo(expectedPointers);
    }

    @Nested
    class OverlapsWithAny {

        @ParameterizedTest
        @ArgumentsSource(ShouldOverlapWithAnyTestData.class)
        void shouldOverlapsWithAnyToBeTrue(long[] checkedDates, long[][] sortedRangeOfDates) {
            assertThat(overlapsWithAny(checkedDates, sortedRangeOfDates)).isTrue();
        }

        @ParameterizedTest
        @ArgumentsSource(ShouldNotOverlapWithAnyTestData.class)
        void shouldOverlapsWithAnyToBeFalse(long[] checkedDates, long[][] sortedRangeOfDates) {
            assertThat(overlapsWithAny(checkedDates, sortedRangeOfDates)).isFalse();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class Overlaps {
        @ParameterizedTest
        @MethodSource("provideOverlappingIntervals")
        void shouldReturnTrueWhenIntervalsOverlap2(
                long[] checkedInterval,
                long[] comparativeInterval
        ) {
            assertThat(CollisionDetection.overlaps(checkedInterval, comparativeInterval)).isTrue();
        }

        private Stream<Arguments> provideOverlappingIntervals() {
            return Stream.of(
                    Arguments.of(new long[]{4, 6}, new long[]{5, 7}),
                    Arguments.of(new long[]{5, 7}, new long[]{4, 6}),
                    Arguments.of(new long[]{4, 5}, new long[]{3, 6}),
                    Arguments.of(new long[]{3, 6}, new long[]{4, 5})
            );
        }


        @ParameterizedTest
        @MethodSource("provideNoOverlappingIntervals")
        void shouldReturnFalseWhenIntervalsDoNotOverlap(
                long[] checkedInterval,
                long[] comparativeInterval
        ) {
            assertThat(CollisionDetection.overlaps(checkedInterval, comparativeInterval)).isFalse();
        }

        private Stream<Arguments> provideNoOverlappingIntervals() {
            return Stream.of(
                    Arguments.of(new long[]{4, 5}, new long[]{6, 7}),
                    Arguments.of(new long[]{4, 5}, new long[]{5, 7}),
                    Arguments.of(new long[]{4, 5}, new long[]{3, 4}),
                    Arguments.of(new long[]{4, 5}, new long[]{1, 2})
            );
        }
    }

}