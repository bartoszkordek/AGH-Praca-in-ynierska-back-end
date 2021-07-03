package com.healthy.gym.account.pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTest {

    private Image image1;
    private Image image2;

    @BeforeEach
    void setUp() {
        image1 = new Image();
        image2 = new Image();
    }

    @Test
    void shouldAllFieldBeNullWhenCreated() {
        assertThat(image1).hasAllNullFieldsOrProperties();
    }

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        assertThat(image1).isEqualTo(image2);
    }

    @Test
    void shouldHaveSameHashCodeWhenAllFieldAreTheSame() {
        assertThat(image1).hasSameHashCodeAs(image2);
    }

    @Test
    void shouldNotBeEqualWhenAllFieldAreTheSame() {
        image1.setFormat("image/jpeg");
        assertThat(image1).isNotEqualTo(image2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreTheSame() {
        image1.setFormat("image/jpeg");
        assertThat(image1.hashCode()).isNotEqualTo(image2.hashCode());
    }
}