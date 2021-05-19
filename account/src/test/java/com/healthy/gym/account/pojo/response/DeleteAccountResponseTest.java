package com.healthy.gym.account.pojo.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteAccountResponseTest {

    private DeleteAccountResponse response;
    private DeleteAccountResponse response2;

    @BeforeEach
    void setUp() {
        response = new DeleteAccountResponse("test");
        response2 = new DeleteAccountResponse("test");
    }

    @Test
    void shouldAllFieldNotBeNullWhenCreated() {
        assertThat(response).hasNoNullFieldsOrProperties();
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
    void shouldNotBeEqualWhenAllFieldAreNotTheSame() {
        response.setMessage("test2");
        assertThat(response).isNotEqualTo(response2);
    }

    @Test
    void shouldNotHaveSameHashCodeWhenAllFieldAreNotTheSame() {
        response.setMessage("test2");
        assertThat(response.hashCode()).isNotEqualTo(response2.hashCode());
    }
}