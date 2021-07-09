package com.healthy.gym.auth.data.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrivacyDocumentTest {
    private UserPrivacyDocument userPrivacyDocument1;
    private UserPrivacyDocument userPrivacyDocument2;

    @BeforeEach
    void setUp() {
        userPrivacyDocument1 = new UserPrivacyDocument();
        userPrivacyDocument2 = new UserPrivacyDocument();
    }

    @Test
    void twoObjectsOfEmptyUserPrivacyDocumentShouldBeEqual() {
        assertThat(userPrivacyDocument2)
                .isEqualTo(userPrivacyDocument1)
                .hasSameHashCodeAs(userPrivacyDocument1);
    }

    @Test
    void twoDifferentObjectsOfUserPrivacyDocumentShouldNotBeEqual() {
        userPrivacyDocument1.setAllowShowingAvatar(true);
        assertThat(userPrivacyDocument1).isNotEqualTo(userPrivacyDocument2);
    }

    @Test
    void whenCreatedHaveAllFieldNullExceptBooleanLong() {
        assertThat(new UserPrivacyDocument()).hasAllNullFieldsOrPropertiesExcept(
                "regulationsAccepted", "allowShowingTrainingsParticipation",
                "allowShowingUserStatistics", "allowShowingAvatar"
        );
    }
}