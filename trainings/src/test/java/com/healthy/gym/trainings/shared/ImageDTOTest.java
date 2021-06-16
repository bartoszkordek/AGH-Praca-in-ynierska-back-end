package com.healthy.gym.trainings.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageDTOTest {
    private ImageDTO imageDTO1;
    private ImageDTO imageDTO2;

    @BeforeEach
    void setUp() {
        imageDTO1 = new ImageDTO();
        imageDTO2 = new ImageDTO();
    }

    @Test
    void shouldAllFieldsBeNull() {
        assertThat(imageDTO1).hasAllNullFieldsOrProperties();
    }

    @Test
    void twoEmptyObjectShouldBeEqual() {
        assertThat(imageDTO1).hasSameHashCodeAs(imageDTO2).isEqualTo(imageDTO2);
    }

    @Test
    void twoEmptyObjectWithSameFieldShouldBeEqual() {
        imageDTO1 = new ImageDTO("data", "format");
        imageDTO2 = new ImageDTO("data", "format");
        assertThat(imageDTO1).hasSameHashCodeAs(imageDTO2).isEqualTo(imageDTO2);
    }

    @Test
    void twoEmptyObjectWithDifferentFieldShouldBeEqual() {
        imageDTO1 = new ImageDTO("data", "format");
        imageDTO2 = new ImageDTO("data2", "format2");
        assertThat(imageDTO1).isNotEqualTo(imageDTO2);
    }
}