package com.healthy.gym.user.security;

import com.healthy.gym.user.component.HttpHeaderParser;
import com.healthy.gym.user.component.TokenValidator;
import org.springframework.core.env.Environment;
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

    private final Environment environment;
    private final TokenValidator tokenValidator;
    private final HttpHeaderParser httpHeaderParser;

    public JWTAuthenticationFilter(
            AuthenticationManager authenticationManager,
            Environment environment,
            TokenValidator tokenValidator,
            HttpHeaderParser httpHeaderParser
    ) {
        super(authenticationManager);
        this.environment = environment;
        this.tokenValidator = tokenValidator;
        this.httpHeaderParser = httpHeaderParser;
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
            UsernamePasswordAuthenticationToken authenticationToken =
                    tokenValidator.getAuthentication(token, getTokenPrefix(), getSigningKey());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            chain.doFilter(request, response);
        }
    }

    private String getTokenPrefix() {
        return environment.getRequiredProperty("authorization.token.header.prefix");
    }

    private String getSigningKey() {
        return environment.getRequiredProperty("token.secret");
    }

}
