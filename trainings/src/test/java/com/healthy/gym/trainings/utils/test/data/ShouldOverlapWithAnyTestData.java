package com.healthy.gym.trainings.utils.test.data;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class ShouldOverlapWithAnyTestData implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        new long[]{10, 13},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{4, 5},
                                new long[]{6, 8},
                                new long[]{8, 9},
                                new long[]{10, 11},
                                new long[]{12, 13},
                                new long[]{14, 15},
                                new long[]{20, 30}
                        }
                ),
                Arguments.of(
                        new long[]{4, 13},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{8, 9},
                                new long[]{20, 30},
                        }
                ),
                Arguments.of(
                        new long[]{1, 100},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{8, 9},
                                new long[]{20, 30},
                        }
                ),
                Arguments.of(
                        new long[]{15, 25},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{8, 9},
                                new long[]{20, 30},
                        }
                ),
                Arguments.of(
                        new long[]{22, 25},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{8, 9},
                                new long[]{20, 30},
                                new long[]{31, 34},
                                new long[]{40, 45},
                                new long[]{50, 51},
                                new long[]{60, 70},
                                new long[]{90, 99}
                        }
                ),
                Arguments.of(
                        new long[]{25, 35},
                        new long[][]{
                                new long[]{2, 3},
                                new long[]{8, 9},
                                new long[]{20, 30},
                                new long[]{31, 34},
                                new long[]{40, 45},
                                new long[]{50, 51},
                                new long[]{60, 70},
                                new long[]{90, 99}
                        }
                )
        );
    }
}
