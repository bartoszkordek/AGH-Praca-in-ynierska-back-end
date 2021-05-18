package com.healthy.gym.account.pojo.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordRequestTest {

    private ChangePasswordRequest request;
    private ChangePasswordRequest request2;

    @BeforeEach
    void setUp() {
        request = new ChangePasswordRequest();
        request2 = new ChangePasswordRequest();
    }

    @Test
    void shouldAllFieldBeNullWhenCreated() {
        assertThat(request).hasAllNullFieldsOrProperties();
    }

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        assertThat(request).isEqualTo(request2);
    }

    @Test
    void shouldHaveSameHashCodeWhenAllFieldAreTheSame() {
        assertThat(request).hasSameHashCodeAs(request2);
    }

    @Test
    void shouldNotBeEqualWhenAllFieldAreTheSame() {
        request.setNewPassword("test");
        assertThat(request).isNotEqualTo(request2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreTheSame() {
        request.setNewPassword("test");
        assertThat(request.hashCode()).isNotEqualTo(request2.hashCode());
    }
}