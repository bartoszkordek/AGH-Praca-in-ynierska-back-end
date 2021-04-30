package com.healthy.gym.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.component.TokenValidator;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.pojo.response.LogoutResponse;
import io.jsonwebtoken.Jwts;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
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
    private final Environment environment;
    private final TokenValidator tokenValidator;
    private final Translator translator;

    public RedisLogoutHandler(
            RedisTemplate<String, String> redisTemplate,
            Environment environment,
            TokenValidator tokenValidator,
            Translator translator
    ) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.tokenValidator = tokenValidator;
        this.translator = translator;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String headerName;
        String headerPrefix;
        String signingKey;

        try {
            headerName = environment.getRequiredProperty("authorization.token.header.name");
            headerPrefix = environment.getRequiredProperty("authorization.token.header.prefix");
            signingKey = environment.getRequiredProperty("token.secret");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        String token = request.getHeader(headerName);
        if (token == null || !token.startsWith(headerPrefix)) return;

        try {
            invalidateToken(response, headerPrefix, signingKey, token);
            handleSuccessfulLogout(response);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void invalidateToken(
            HttpServletResponse response,
            String headerPrefix,
            String signingKey,
            String token
    ) throws IOException {
        try {
            Date expirationTime = tokenValidator.getTokenExpirationTime(token, headerPrefix, signingKey);
            var duration = Duration.ofMillis(expirationTime.getTime() - System.currentTimeMillis());
            token = tokenValidator.purifyToken(token, headerPrefix);
            String userId = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getSubject();
            redisTemplate.opsForValue().set(token, userId, duration);
        } catch (Exception e) {
            e.printStackTrace();
            handleUnsuccessfulLogout(response);
        }
    }

    private void handleUnsuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.fail");
        Map<String, String> errors = new HashMap<>();
        errors.put("Token", "Invalid token");

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, false));

        handleResponse(response, HttpStatus.UNAUTHORIZED, bodyAsString);
    }

    private void handleSuccessfulLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.success");
        Map<String, String> errors = new HashMap<>();

        var objectMapper = new ObjectMapper();
        var bodyAsString = objectMapper
                .writeValueAsString(new LogoutResponse(message, errors, true));

        handleResponse(response, HttpStatus.OK, bodyAsString);
    }

    private void handleResponse(HttpServletResponse response, HttpStatus status, String body) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setLocale(LocaleContextHolder.getLocale());
        response.getWriter().println(body);
    }


}
