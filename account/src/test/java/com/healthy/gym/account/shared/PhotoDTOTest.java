package com.healthy.gym.account.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhotoDTOTest {

    private PhotoDTO photoDTO1;
    private PhotoDTO photoDTO2;

    @BeforeEach
    void setUp() {
        photoDTO1 = new PhotoDTO();
        photoDTO2 = new PhotoDTO();
    }

    @Test
    void shouldBeEqualWhenAllFieldTheSame() {
        assertThat(photoDTO1).isEqualTo(photoDTO2).hasSameHashCodeAs(photoDTO2);
    }

    @Test
    void shouldNotBeEqualWhenAnyFieldDifferent() {
        photoDTO1.setTitle("Avatar");
        assertThat(photoDTO1).isNotEqualTo(photoDTO2);
    }
}