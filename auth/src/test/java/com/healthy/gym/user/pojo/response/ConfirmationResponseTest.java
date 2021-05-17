package com.healthy.gym.user.pojo.response;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationResponseTest {

    @Test
    void whenCreatedIsNotNull() {
        assertThat(new ConfirmationResponse()).isNotNull();
    }

    @Test
    void whenCreatedWithDefaultConstructorAllFieldAreNullExceptSuccess() {
        ConfirmationResponse response = new ConfirmationResponse();
        assertThat(response).hasAllNullFieldsOrPropertiesExcept("success");
        assertThat(response.isSuccess()).isFalse();
    }

    @Nested
    class objectsShouldBeEqualWhen {

        @Test
        void theyAreBothCreatedByDefaultConstructor() {
            ConfirmationResponse response1 = new ConfirmationResponse();
            ConfirmationResponse response2 = new ConfirmationResponse();

            assertThat(response1)
                    .hasSameHashCodeAs(response2)
                    .isEqualTo(response2);
        }

        @Test
        void theyAreBothCreatedWithTheSameInitialValues() {
            ConfirmationResponse response1 =
                    new ConfirmationResponse("Success message", new HashMap<>(), true);
            ConfirmationResponse response2 =
                    new ConfirmationResponse("Success message", new HashMap<>(), true);

            assertThat(response1)
                    .hasSameHashCodeAs(response2)
                    .isEqualTo(response2);
        }

        @Test
        void theyHaveTheSameFieldValues() {
            ConfirmationResponse response1 = new ConfirmationResponse();
            response1.setSuccess(true);
            response1.setMessage("Sample message");

            ConfirmationResponse response2 = new ConfirmationResponse();
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
            ConfirmationResponse response1 =
                    new ConfirmationResponse("Success message", new HashMap<>(), true);
            ConfirmationResponse response2 =
                    new ConfirmationResponse("Failure message", new HashMap<>(), false);

            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        void theyHaveDifferentFieldValues() {
            ConfirmationResponse response1 = new ConfirmationResponse();
            response1.setSuccess(false);
            response1.setMessage("Sample message");

            ConfirmationResponse response2 = new ConfirmationResponse();
            response2.setSuccess(true);
            response2.setMessage("Sample message");

            assertThat(response1).isNotEqualTo(response2);
        }
    }

}