package com.healthy.gym.auth.security;

import com.healthy.gym.auth.component.AuthResponseManager;
import com.healthy.gym.auth.component.HttpHeaderParser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RedisFilter extends GenericFilterBean {

    private final HttpHeaderParser httpHeaderParser;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthResponseManager responseManager;

    public RedisFilter(
            HttpHeaderParser httpHeaderParser,
            RedisTemplate<String, String> redisTemplate,
            AuthResponseManager responseManager
    ) {
        this.httpHeaderParser = httpHeaderParser;
        this.redisTemplate = redisTemplate;
        this.responseManager = responseManager;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpHeaderParser.getAuthenticationToken(httpRequest);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        String userID = redisTemplate.opsForValue().get(token);
        if (userID == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        responseManager.handleTokenExpiredLogout(httpResponse);
    }
}
