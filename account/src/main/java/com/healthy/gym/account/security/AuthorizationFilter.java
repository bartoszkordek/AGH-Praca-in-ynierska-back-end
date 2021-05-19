package com.healthy.gym.account.security;

import com.healthy.gym.account.component.token.TokenManager;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenManager tokenManager;

    public AuthorizationFilter(
            AuthenticationManager authenticationManager,
            TokenManager tokenManager
    ) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String headerName = tokenManager.getHttpHeaderName();
        if (headerName == null) return;

        String authorizationHeader = request.getHeader(headerName);

        String headerPrefix = tokenManager.getTokenPrefix();
        if (headerPrefix == null) return;

        if (authorizationHeader == null || !authorizationHeader.startsWith(headerPrefix)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                getAuthentication(authorizationHeader, headerPrefix);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(
            String authorizationHeader,
            String headerPrefix
    ) {
        String token = authorizationHeader
                .replace(headerPrefix, "")
                .trim();

        String signingKey = tokenManager.getSigningKey();
        if (signingKey == null) return null;

        String userId = Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        if (userId == null) return null;

        return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
    }
}
