package com.healthy.gym.gateway.security;

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

public class AuthenticationFilter extends BasicAuthenticationFilter {

    private final Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment environment) {
        super(authenticationManager);
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        String headerPropName=environment.getProperty("authorization.token.header.name");
        String authorizationHeader=request.getHeader(headerPropName);
        String headerPropPrefix=environment.getProperty("authorization.token.header.prefix");

        if(authorizationHeader==null || !authorizationHeader.startsWith(headerPropPrefix)){
            chain.doFilter(request,response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken=getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request,response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request){
        String headerPropName=environment.getProperty("authorization.token.header.name");
        String authorizationHeader=request.getHeader(headerPropName);
        if(authorizationHeader==null) return null;

        String headerPropPrefix=environment.getProperty("authorization.token.header.prefix");
        if(headerPropPrefix==null) return null;

        String token=authorizationHeader.replace(headerPropPrefix,"");

        String secretToken=environment.getProperty("token.secret");

        String userId= Jwts.parser()
                .setSigningKey(secretToken)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        if(userId==null) return null;

        return new UsernamePasswordAuthenticationToken(userId,null,new ArrayList<>());
    }
}
