package com.healthy.gym.user.data.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RegistrationTokenTest {

    @Nested
    class WhenCreated {
        @Test
        void shouldHaveAllFieldNullOrEqual0() {
            RegistrationToken verificationToken = new RegistrationToken();
            assertThat(verificationToken).hasAllNullFieldsOrPropertiesExcept("id","wasUsed");
            assertThat(verificationToken.getId()).isZero();
        }

        @Test
        void shouldHaveAllFieldNullOrEqual0ExceptToken() {
            UserEntity userEntity = mock(UserEntity.class);
            String token = UUID.randomUUID().toString();
            RegistrationToken verificationToken = new RegistrationToken(token, userEntity);
            assertThat(verificationToken).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    class ExpirationTime {

        private RegistrationToken verificationToken;
        private LocalDateTime expirationTime;

        @BeforeEach
        void setUp() {
            UserEntity userEntity = mock(UserEntity.class);
            String token = UUID.randomUUID().toString();
            verificationToken = new RegistrationToken(token, userEntity);
            expirationTime = LocalDateTime.now().plusDays(1);
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