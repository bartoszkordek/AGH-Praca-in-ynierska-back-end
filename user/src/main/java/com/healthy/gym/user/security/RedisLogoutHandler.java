package com.healthy.gym.user.security;

import com.healthy.gym.user.component.TokenValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Date;

public class RedisLogoutHandler implements LogoutHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private final Environment environment;
    private final TokenValidator tokenValidator;

    public RedisLogoutHandler(
            RedisTemplate<String, String> redisTemplate,
            Environment environment,
            TokenValidator tokenValidator
    ) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.tokenValidator = tokenValidator;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String headerName = environment.getProperty("authorization.token.header.name");
        if (headerName == null) return;

        String headerPrefix = environment.getProperty("authorization.token.header.prefix");
        if (headerPrefix == null) return;

        String authorizationHeader = request.getHeader(headerName);

        if (authorizationHeader == null || !authorizationHeader.startsWith(headerPrefix)) return;

        getTokenExpirationTime(authorizationHeader, headerPrefix);

        tokenValidator.getTokenExpirationTime("","");
//
//        try{
////            tokenValidator.getTokenExpirationTime(
////
////            redisTemplate.opsForValue().set();
//        }catch ()
//

    }

    private Duration getTokenExpirationTime(
            String authorizationHeader,
            String headerPrefix
    ) {
        String token = authorizationHeader
                .replace(headerPrefix, "")
                .trim();

        String secretToken = environment.getProperty("token.secret");
        if (secretToken == null) return null;

        Date expiration = Jwts.parser()
                .setSigningKey(secretToken)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        if (expiration == null) return null;

        return null;
    }
}
