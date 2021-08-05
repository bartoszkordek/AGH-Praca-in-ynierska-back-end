package com.healthy.gym.gympass.component;

import io.jsonwebtoken.SignatureAlgorithm;

public interface TokenManager {
    String getSigningKey();

    long getExpirationTimeInMillis();

    String getHttpHeaderName();

    String getTokenPrefix();

    SignatureAlgorithm getSignatureAlgorithm();
}
