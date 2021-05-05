package com.healthy.gym.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.component.HttpHeaderParser;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.pojo.response.LogoutResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class RedisFilter extends GenericFilterBean {

    private final Translator translator;
    private final HttpHeaderParser httpHeaderParser;
    private final RedisTemplate<String, String> redisTemplate;

    public RedisFilter(
            Translator translator,
            HttpHeaderParser httpHeaderParser,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.translator = translator;
        this.httpHeaderParser = httpHeaderParser;
        this.redisTemplate = redisTemplate;
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
        handleTokenExpiredLogout(httpResponse);
    }

    private void handleTokenExpiredLogout(HttpServletResponse response) throws IOException {
        String message = translator.toLocale("user.logout.token.expired");
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
