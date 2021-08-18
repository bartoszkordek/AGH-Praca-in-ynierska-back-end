package com.healthy.gym.account.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrivacyDTOTest {
    private UserPrivacyDTO userPrivacyDTO1;
    private UserPrivacyDTO userPrivacyDTO2;

    @BeforeEach
    void setUp() {
        userPrivacyDTO1 = new UserPrivacyDTO();
        userPrivacyDTO2 = new UserPrivacyDTO();
    }

    @Test
    void twoEmptyUserDTOShouldBeEqual() {
        assertThat(userPrivacyDTO1)
                .isEqualTo(userPrivacyDTO2)
                .hasSameHashCodeAs(userPrivacyDTO2);
    }

    @Test
    void twoEmptyUserDTOShouldNotBeEqual() {
        userPrivacyDTO1.setAllowShowingAvatar(true);
        assertThat(userPrivacyDTO1).isNotEqualTo(userPrivacyDTO2);
    }

}