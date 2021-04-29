package com.healthy.gym.user.component;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;

public interface TokenValidator {
    Date getTokenExpirationTime(String token, String signingKey);

    Date getTokenExpirationTime(String token, String tokenPrefix, String signingKey);

    UsernamePasswordAuthenticationToken getAuthentication(String token, String signingKey);

    UsernamePasswordAuthenticationToken getAuthentication(String token, String tokenPrefix, String signingKey);

    String purifyToken(String token, String tokenPrefix);
}
