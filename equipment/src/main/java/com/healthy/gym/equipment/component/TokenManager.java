package com.healthy.gym.equipment.component;

import io.jsonwebtoken.SignatureAlgorithm;

public interface TokenManager {
    String getSigningKey();

    long getExpirationTimeInMillis();

    String getHttpHeaderName();

    String getTokenPrefix();

    SignatureAlgorithm getSignatureAlgorithm();
}
