package com.healthy.gym.auth.component.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

@Component
public class TokenValidatorImpl implements TokenValidator {

    private static final String INVALID_TOKEN = "Invalid token.";

    @Override
    public Date getTokenExpirationTime(String token, String signingKey) {
        return getTokenExpirationTime(token, null, signingKey);
    }

    @Override
    public Date getTokenExpirationTime(String token, String tokenPrefix, String signingKey) {
        validateArguments(token, signingKey);
        String pureToken = purifyTokenInternal(token, tokenPrefix);

        Date expiration;

        try {
            expiration = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(pureToken)
                    .getBody()
                    .getExpiration();
        } catch (ExpiredJwtException exception) {
            throw new ExpiredJwtException(null, null, exception.getMessage());
        } catch (Exception exception) {
            throw new AuthenticationServiceException(INVALID_TOKEN);
        }

        isTokenValid(expiration);

        return expiration;
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token, String signingKey) {
        return getAuthentication(token, null, signingKey);
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token, String tokenPrefix, String signingKey) {
        validateArguments(token, signingKey);
        String pureToken = purifyTokenInternal(token, tokenPrefix);

        String userId;

        try {
            userId = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(pureToken)
                    .getBody()
                    .getSubject();
        } catch (Exception exception) {
            throw new AuthenticationServiceException(INVALID_TOKEN);
        }

        isTokenValid(userId);

        return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
    }

    @Override
    public String purifyToken(String token, String tokenPrefix) {
        isTokenNull(token);
        return purifyTokenInternal(token, tokenPrefix);
    }

    private String purifyTokenInternal(String token, String prefix) {
        if (prefix == null) return token.trim();
        return token.replace(prefix, "").trim();
    }

    private void validateArguments(String token, String signingKey) {
        isTokenNull(token);
        isSigningKeyNull(signingKey);
    }

    private void isTokenValid(Object token) {
        if (token == null) throw new AuthenticationServiceException(INVALID_TOKEN);
    }

    private void isTokenNull(String token) {
        if (token == null) throw new IllegalArgumentException("Token can not be null.");
    }

    private void isSigningKeyNull(String signingKey) {
        if (signingKey == null) throw new IllegalArgumentException("Signing key can not be null.");
    }
}
