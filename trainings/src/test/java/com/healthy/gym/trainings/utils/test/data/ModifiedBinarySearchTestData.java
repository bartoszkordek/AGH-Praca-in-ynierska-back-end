package com.healthy.gym.trainings.utils.test.data;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class ModifiedBinarySearchTestData implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        11,
                        new long[][]{
                                new long[]{2},
                                new long[]{4},
                                new long[]{6},
                                new long[]{8},
                                new long[]{10},
                                new long[]{12},
                                new long[]{14},
                                new long[]{16},
                                new long[]{17},
                                new long[]{19}
                        },
                        new int[]{4, 5}
                ),
                Arguments.of(
                        3,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{0, 1}
                ),
                Arguments.of(
                        2,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{0, 1}
                ),
                Arguments.of(
                        1,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{-1, 0}
                ),
                Arguments.of(
                        9,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{3, 4}
                ),
                Arguments.of(
                        11,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{4, -1}
                ),
                Arguments.of(
                        20,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{4, -1}
                ),
                Arguments.of(
                        6,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 11},
                                new long[]{6, 11},
                                new long[]{8, 11},
                                new long[]{10, 11}
                        },
                        new int[]{2, 3}
                ),
                Arguments.of(
                        1,
                        new long[][]{
                                new long[]{2, 5}
                        },
                        new int[]{-1, 0}
                ),
                Arguments.of(
                        3,
                        new long[][]{
                                new long[]{2, 5}
                        },
                        new int[]{0, -1}
                ),
                Arguments.of(
                        3,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 5}
                        },
                        new int[]{0, 1}
                ),
                Arguments.of(
                        2,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 5}
                        },
                        new int[]{0, 1}
                ),
                Arguments.of(
                        4,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 5}
                        },
                        new int[]{1, -1}
                ),
                Arguments.of(
                        5,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 5}
                        },
                        new int[]{1, -1}
                ),
                Arguments.of(
                        1,
                        new long[][]{
                                new long[]{2, 5},
                                new long[]{4, 5}
                        },
                        new int[]{-1, 0}
                )
        );
    }
}
