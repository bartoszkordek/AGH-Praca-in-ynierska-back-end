package com.healthy.gym.user.component.token;

public interface TokenManager {
    String getSigningKey();

    long getExpirationTimeInMillis();

    String getHttpHeaderName();

    String getTokenPrefix();
}
