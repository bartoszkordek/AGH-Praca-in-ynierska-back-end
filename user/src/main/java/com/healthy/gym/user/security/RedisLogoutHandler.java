package com.healthy.gym.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.component.TokenValidator;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.pojo.response.LogoutResponse;
import io.jsonwebtoken.Jwts;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RedisLogoutHandler implements LogoutSuccessHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenValidator tokenValidator;
    private final Translator translator;
    private final TokenManager tokenManager;

    public RedisLogoutHandler(
            RedisTemplate<String, String> redisTemplate,
            TokenValidator tokenValidator,
            Translator translator,
            TokenManager tokenManager
    ) {
        this.redisTemplate = redisTemplate;
        this.tokenValidator = tokenValidator;
        this.translator = translator;
        this.tokenManager = tokenManager;
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
            handleUnsuccessfulLogout(response);
            return;
        }

        invalidateToken(response, headerPrefix, token);
    }

    private void handleUnsuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.fail");
        Map<String, String> errors = new HashMap<>();
        errors.put("token", translator.toLocale("user.logout.invalid.token"));

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, false));

        handleResponse(response, HttpStatus.UNAUTHORIZED, bodyAsString);
    }

    private void handleResponse(HttpServletResponse response, HttpStatus status, String body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setLocale(LocaleContextHolder.getLocale());
        response.getWriter().println(body);
    }

    private void invalidateToken(HttpServletResponse response, String headerPrefix, String token) throws IOException {

        try {
            String signingKey = tokenManager.getSigningKey();
            Date expirationTime = tokenValidator.getTokenExpirationTime(token, headerPrefix, signingKey);

            Duration durationToExpireToken = getTokenDurationToBeExpired(expirationTime);
            if (durationToExpireToken.isNegative()) handleTokenExpiredLogout(response);

            token = tokenValidator.purifyToken(token, headerPrefix);

            redisTemplate.opsForValue().set(
                    token,
                    getUserId(token, signingKey),
                    durationToExpireToken
            );

            handleSuccessfulLogout(response);
        } catch (Exception e) {
            e.printStackTrace();
            handleUnsuccessfulLogout(response);
        }
    }

    private void handleTokenExpiredLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.token.expired");
        Map<String, String> errors = new HashMap<>();

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, true));

        handleResponse(response, HttpStatus.OK, bodyAsString);
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

    private void handleSuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.success");
        Map<String, String> errors = new HashMap<>();

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, true));

        handleResponse(response, HttpStatus.OK, bodyAsString);
    }


}
