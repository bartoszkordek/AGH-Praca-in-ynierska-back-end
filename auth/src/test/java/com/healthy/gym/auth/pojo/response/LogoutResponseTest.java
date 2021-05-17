package com.healthy.gym.auth.pojo.response;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutResponseTest {

    @Test
    void whenCreatedIsNotNull() {
        assertThat(new LogoutResponse()).isNotNull();
    }

    @Test
    void whenCreatedWithDefaultConstructorAllFieldAreNullExceptSuccess() {
        LogoutResponse response =new LogoutResponse();
        assertThat(response).hasAllNullFieldsOrPropertiesExcept("success");
        assertThat(response.isSuccess()).isFalse();
    }

    @Nested
    class objectsShouldBeEqualWhen {

        @Test
        void theyAreBothCreatedByDefaultConstructor() {
            LogoutResponse response1 = new LogoutResponse();
            LogoutResponse response2 = new LogoutResponse();

            assertThat(response1)
                    .hasSameHashCodeAs(response2)
                    .isEqualTo(response2);
        }

        @Test
        void theyAreBothCreatedWithTheSameInitialValues() {
            LogoutResponse response1 = new LogoutResponse("Success message", new HashMap<>(), true);
            LogoutResponse response2 = new LogoutResponse("Success message", new HashMap<>(), true);

            assertThat(response1)
                    .hasSameHashCodeAs(response2)
                    .isEqualTo(response2);
        }

        @Test
        void theyHaveTheSameFieldValues() {
            LogoutResponse response1 = new LogoutResponse();
            response1.setSuccess(true);
            response1.setMessage("Sample message");

            LogoutResponse response2 = new LogoutResponse();
            response2.setSuccess(true);
            response2.setMessage("Sample message");

            assertThat(response1)
                    .hasSameHashCodeAs(response2)
                    .isEqualTo(response2);
        }
    }

    @Nested
    class objectShouldNotBeEqualWhen {

        @Test
        void theyAreBothCreatedWithDifferentInitialValues() {
            LogoutResponse response1 = new LogoutResponse("Success message", new HashMap<>(), true);
            LogoutResponse response2 = new LogoutResponse("Failure message", new HashMap<>(), false);

            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        void theyHaveDifferentFieldValues() {
            LogoutResponse response1 = new LogoutResponse();
            response1.setSuccess(false);
            response1.setMessage("Sample message");

            LogoutResponse response2 = new LogoutResponse();
            response2.setSuccess(true);
            response2.setMessage("Sample message");

            assertThat(response1).isNotEqualTo(response2);
        }
    }
}