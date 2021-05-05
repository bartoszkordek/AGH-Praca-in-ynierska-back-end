package com.healthy.gym.user.security;

import com.healthy.gym.user.component.AuthResponseManager;
import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.component.token.TokenValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;

@Service
public class RedisLogoutHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenValidator tokenValidator;
    private final TokenManager tokenManager;
    private final AuthResponseManager responseManager;

    @Autowired
    public RedisLogoutHandler(
            RedisTemplate<String, String> redisTemplate,
            TokenValidator tokenValidator,
            TokenManager tokenManager,
            AuthResponseManager responseManager
    ) {
        this.redisTemplate = redisTemplate;
        this.tokenValidator = tokenValidator;
        this.tokenManager = tokenManager;
        this.responseManager = responseManager;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String headerName = tokenManager.getHttpHeaderName();
        String headerPrefix = tokenManager.getTokenPrefix();

        String token = request.getHeader(headerName);
        if (token == null || !token.startsWith(headerPrefix)) {
            responseManager.handleUnsuccessfulLogout(response);
            return;
        }

        invalidateToken(response, headerPrefix, token);
    }

    private void invalidateToken(HttpServletResponse response, String headerPrefix, String token) throws IOException {

        try {
            String signingKey = tokenManager.getSigningKey();
            Date expirationTime = tokenValidator.getTokenExpirationTime(token, headerPrefix, signingKey);

            Duration durationToExpireToken = getTokenDurationToBeExpired(expirationTime);

            token = tokenValidator.purifyToken(token, headerPrefix);

            redisTemplate.opsForValue().set(
                    token,
                    getUserId(token, signingKey),
                    durationToExpireToken
            );

            responseManager.handleSuccessfulLogout(response);
        } catch (ExpiredJwtException exception) {
            responseManager.handleTokenExpiredLogout(response);
        } catch (Exception e) {
            responseManager.handleUnsuccessfulLogout(response);
        }
    }

    private String getUserId(String token, String signingKey) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Duration getTokenDurationToBeExpired(Date tokenExpirationTime) {
        return Duration.ofMillis(tokenExpirationTime.getTime() - System.currentTimeMillis());
    }
}
