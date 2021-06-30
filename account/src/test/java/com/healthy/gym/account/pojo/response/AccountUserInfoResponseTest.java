package com.healthy.gym.account.pojo.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountUserInfoResponseTest {

    @Test
    void shouldBeEqualWhenAllFieldAreTheSame() {
        AccountUserInfoResponse response = new AccountUserInfoResponse(
                "testMessage",
                "testName",
                "testSurname",
                "testEmail",
                "testPhone"
        );
        AccountUserInfoResponse response2 = new AccountUserInfoResponse(
                "testMessage",
                "testName",
                "testSurname",
                "testEmail",
                "testPhone"
        );
        assertThat(response).isEqualTo(response2);
    }

    @Test
    void shouldNotBeEqualWhenSomeFieldsAreDifferent() {
        AccountUserInfoResponse response = new AccountUserInfoResponse(
                "testMessage2",
                "testName",
                "testSurname",
                "testEmail",
                "testPhone"
        );
        AccountUserInfoResponse response2 = new AccountUserInfoResponse(
                "testMessage",
                "testName",
                "testSurname",
                "testEmail",
                "testPhone"
        );
        assertThat(response).isNotEqualTo(response2);
    }
}