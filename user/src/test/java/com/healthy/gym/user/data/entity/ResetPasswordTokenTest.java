package com.healthy.gym.user.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ResetPasswordTokenTest {

    @Nested
    class WhenCreated {
        @Test
        void shouldHaveAllFieldNullOrEqual0() {
            ResetPasswordToken token = new ResetPasswordToken();
            assertThat(token).hasAllNullFieldsOrPropertiesExcept("id");
            assertThat(token.getId()).isZero();
        }

        @Test
        void shouldHaveAllFieldNullOrEqual0ExceptToken() {
            UserEntity userEntity = mock(UserEntity.class);
            String token = UUID.randomUUID().toString();
            ResetPasswordToken verificationToken = new ResetPasswordToken(token, userEntity);
            assertThat(verificationToken).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    class ExpirationTime {

        private ResetPasswordToken verificationToken;
        private LocalDateTime expirationTime;

        @BeforeEach
        void setUp() {
            UserEntity userEntity = mock(UserEntity.class);
            String token = UUID.randomUUID().toString();
            verificationToken = new ResetPasswordToken(token, userEntity);
            expirationTime = LocalDateTime.now().plusHours(2);
        }

        @Test
        void shouldBeNextDay() {
            int actualExpirationDay = verificationToken.getExpiryDate().getDayOfMonth();
            int expectedExpirationDay = expirationTime.getDayOfMonth();
            assertThat(actualExpirationDay).isEqualTo(expectedExpirationDay);
        }

        @Test
        void shouldBeTheSameHour() {
            int actualExpirationHour = verificationToken.getExpiryDate().getHour();
            int expectedExpirationHour = expirationTime.getHour();
            assertThat(actualExpirationHour).isEqualTo(expectedExpirationHour);
        }

        @Test
        void shouldBeTheSameMinute() {
            int actualExpirationMinute = verificationToken.getExpiryDate().getMinute();
            int expectedExpirationMinute = expirationTime.getMinute();
            assertThat(actualExpirationMinute).isEqualTo(expectedExpirationMinute);
        }

        @Test
        void shouldBeTheSameSecond() {
            int actualExpirationSecond = verificationToken.getExpiryDate().getSecond();
            int expectedExpirationSecond = expirationTime.getSecond();
            assertThat(actualExpirationSecond).isEqualTo(expectedExpirationSecond);
        }
    }


}