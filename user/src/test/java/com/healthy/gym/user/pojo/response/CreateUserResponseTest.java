package com.healthy.gym.user.pojo.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserResponseTest {
    private CreateUserResponse response1;
    private CreateUserResponse response2;

    @BeforeEach
    void setUp() {
        response1 = new CreateUserResponse();
        response2 = new CreateUserResponse();
    }

    @Test
    void twoObjectsOfEmptyCreateUserResponseShouldBeEqual() {
        assertThat(response1)
                .isEqualTo(response2)
                .hasSameHashCodeAs(response2);
    }

    @Test
    void twoObjectsOfCreateUserResponseWithSameFieldValuesShouldBeEqual() {
        response1.setUserId("randomID");
        response1.setSuccess(true);
        response2.setUserId("randomID");
        response2.setSuccess(true);

        assertThat(response1)
                .isEqualTo(response2)
                .hasSameHashCodeAs(response2);
    }

    @Test
    void twoObjectsOfCreateUserResponseWithSameFieldValuesShouldNotBeEqual() {
        response1.setUserId("randomID");
        response1.setSuccess(true);
        response2.setUserId("randomID");
        response2.setSuccess(false);

        assertThat(response1).isNotEqualTo(response2);
    }
}