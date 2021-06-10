package com.healthy.gym.account.component;

import io.jsonwebtoken.SignatureAlgorithm;

public interface TokenManager {
    String getSigningKey();

    long getExpirationTimeInMillis();

    String getHttpHeaderName();

    String getTokenPrefix();

    SignatureAlgorithm getSignatureAlgorithm();
}
