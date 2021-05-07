package com.healthy.gym.user.security;

import com.healthy.gym.user.component.HttpHeaderParser;
import com.healthy.gym.user.component.token.TokenValidator;
import com.healthy.gym.user.component.token.TokenManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private final TokenValidator tokenValidator;
    private final HttpHeaderParser httpHeaderParser;
    private final TokenManager tokenManager;

    public JWTAuthenticationFilter(
            AuthenticationManager authenticationManager,
            TokenValidator tokenValidator,
            HttpHeaderParser httpHeaderParser,
            TokenManager tokenManager
    ) {
        super(authenticationManager);
        this.tokenValidator = tokenValidator;
        this.httpHeaderParser = httpHeaderParser;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String token = httpHeaderParser.getAuthenticationToken(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String tokenPrefix = tokenManager.getTokenPrefix();
            String signingKey = tokenManager.getSigningKey();

            UsernamePasswordAuthenticationToken authenticationToken =
                    tokenValidator.getAuthentication(token, tokenPrefix, signingKey);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            chain.doFilter(request, response);
        }
    }
}
