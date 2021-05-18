package com.healthy.gym.account.pojo.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordResponseTest {

    private ChangePasswordResponse response;
    private ChangePasswordResponse response2;

    @BeforeEach
    void setUp() {
        response = new ChangePasswordResponse();
        response2 = new ChangePasswordResponse();
    }

    @Test
    void shouldAllFieldsBeNullWhenCreated() {
        assertThat(response).hasAllNullFieldsOrProperties();
    }

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        assertThat(response).isEqualTo(response2);
    }

    @Test
    void shouldHaveSameHashCodeWhenAllFieldAreTheSame() {
        assertThat(response).hasSameHashCodeAs(response2);
    }

    @Test
    void shouldNotBeEqualWhenAllFieldsAreTheSame() {
        response.setMessage("test");
        assertThat(response).isNotEqualTo(response2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreTheSame() {
        response.setMessage("test");
        assertThat(response.hashCode()).isNotEqualTo(response2.hashCode());
    }

}