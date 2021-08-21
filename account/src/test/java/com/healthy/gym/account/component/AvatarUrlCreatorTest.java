package com.healthy.gym.account.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvatarUrlCreatorTest {

    private AvatarUrlCreator avatarUrlCreator;

    @BeforeEach
    void setUp() {
        Environment environment = mock(Environment.class);
        avatarUrlCreator = new AvatarUrlCreatorImpl(environment);
        when(environment.getRequiredProperty("gateway")).thenReturn("http://localhost:8020");
        when(environment.getRequiredProperty("spring.application.name")).thenReturn("account");
    }

    @Test
    void shouldReturnProperUrl() {
        String userId = "772ed48a-dd76-4496-a11f-6ff3453822b0";
        String avatarUrl = avatarUrlCreator.createAvatarUrl(userId);

        assertThat(avatarUrl)
                .isEqualTo("http://localhost:8020/account/photos/772ed48a-dd76-4496-a11f-6ff3453822b0/avatar");
    }
}