package com.healthy.gym.account.pojo.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangeUserDataResponseTest {
    private ChangeUserDataResponse response1;
    private ChangeUserDataResponse response2;

    @BeforeEach
    void setUp() {
        response1 = new ChangeUserDataResponse();
        response2 = new ChangeUserDataResponse();
    }

    @Test
    void shouldAllFieldBeNullWhenCreated() {
        assertThat(response1).hasAllNullFieldsOrProperties();
    }

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        assertThat(response1).isEqualTo(response2);
    }

    @Test
    void shouldHaveSameHashCodeWhenAllFieldAreTheSame() {
        assertThat(response1).hasSameHashCodeAs(response2);
    }

    @Test
    void shouldNotBeEqualWhenAllFieldAreTheSame() {
        response1.setEmail("testemail");
        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreTheSame() {
        response1.setEmail("testemail");
        assertThat(response1.hashCode()).isNotEqualTo(response2.hashCode());
    }
}