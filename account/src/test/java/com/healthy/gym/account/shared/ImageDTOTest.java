package com.healthy.gym.account.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageDTOTest {

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        ImageDTO imageDTO1 = new ImageDTO("TestData", "TestFormat");
        ImageDTO imageDTO2 = new ImageDTO("TestData", "TestFormat");
        assertThat(imageDTO1).isEqualTo(imageDTO2);
    }

    @Test
    void shouldNotBeEqualWhenSomeFieldsAreDifferent() {
        ImageDTO imageDTO1 = new ImageDTO("TestData2", "TestFormat2");
        ImageDTO imageDTO2 = new ImageDTO("TestData", "TestFormat");
        assertThat(imageDTO1).isNotEqualTo(imageDTO2);
    }
}