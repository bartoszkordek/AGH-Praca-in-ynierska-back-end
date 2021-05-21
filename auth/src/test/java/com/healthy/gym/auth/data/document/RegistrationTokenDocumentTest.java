package com.healthy.gym.auth.data.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RegistrationTokenDocumentTest {
    @Nested
    class WhenCreated {
        @Test
        void shouldHaveAllFieldNullOrEqual0() {
            RegistrationTokenDocument verificationToken = new RegistrationTokenDocument();
            assertThat(verificationToken).hasAllNullFieldsOrPropertiesExcept("id", "wasUsed");
        }

        @Test
        void shouldHaveAllFieldNullOrEqual0ExceptToken() {
            UserDocument userDocument = mock(UserDocument.class);
            String token = UUID.randomUUID().toString();
            RegistrationTokenDocument verificationToken = new RegistrationTokenDocument(token, userDocument);
            assertThat(verificationToken).hasNoNullFieldsOrPropertiesExcept("id");
        }
    }

    @Nested
    class ExpirationTime {

        private RegistrationTokenDocument verificationToken;
        private LocalDateTime expirationTime;

        @BeforeEach
        void setUp() {
            UserDocument userDocument = mock(UserDocument.class);
            String token = UUID.randomUUID().toString();
            verificationToken = new RegistrationTokenDocument(token, userDocument);
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