package com.healthy.gym.account.data.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhotoDocumentTest {
    private PhotoDocument photoDocument1;
    private PhotoDocument photoDocument2;

    @BeforeEach
    void setUp() {
        photoDocument1 = new PhotoDocument();
        photoDocument2 = new PhotoDocument();
    }

    @Test
    void shouldBeEqualWhenAllFieldTheSame() {
        assertThat(photoDocument1).isEqualTo(photoDocument2).hasSameHashCodeAs(photoDocument2);
    }

    @Test
    void shouldBeNotEqualWhenAnyFieldIsDifferent() {
        photoDocument1.setTitle("Avatar");
        assertThat(photoDocument1).isNotEqualTo(photoDocument2);
    }
}