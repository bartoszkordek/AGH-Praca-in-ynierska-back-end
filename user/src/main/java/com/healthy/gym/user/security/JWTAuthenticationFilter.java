package com.healthy.gym.user.security;

import io.jsonwebtoken.Jwts;
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
import java.util.ArrayList;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private final Environment environment;

    public JWTAuthenticationFilter(
            AuthenticationManager authenticationManager,
            Environment environment
    ) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String headerName = environment.getProperty("authorization.token.header.name");
        if (headerName == null) return;

        String authorizationHeader = request.getHeader(headerName);

        String headerPrefix = environment.getProperty("authorization.token.header.prefix");
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

        String secretToken = environment.getProperty("token.secret");
        if (secretToken == null) return null;

        String userId = Jwts.parser()
                .setSigningKey(secretToken)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        if (userId == null) return null;

        return new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
    }
}
