package com.healthy.gym.account.pojo.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeUserDataRequestTest {
    private ChangeUserDataRequest request;
    private ChangeUserDataRequest request2;

    @BeforeEach
    void setUp() {
        request = new ChangeUserDataRequest();
        request2 = new ChangeUserDataRequest();
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
        request.setEmail("testemail");
        assertThat(request).isNotEqualTo(request2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreTheSame() {
        request.setEmail("testemail");
        assertThat(request.hashCode()).isNotEqualTo(request2.hashCode());
    }
}