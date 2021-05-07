package com.healthy.gym.user.component.token;

import io.jsonwebtoken.SignatureAlgorithm;

public interface TokenManager {
    String getSigningKey();

    long getExpirationTimeInMillis();

    String getHttpHeaderName();

    String getTokenPrefix();

    SignatureAlgorithm getSignatureAlgorithm();
}
