package com.healthy.gym.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.pojo.request.LogInUserRequest;
import com.healthy.gym.user.service.UserService;
import com.healthy.gym.user.shared.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment environment;

    @Autowired
    public AuthenticationFilter(UserService userService, Environment environment) {
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        try {
            LogInUserRequest credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), LogInUserRequest.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword(),
                            new ArrayList<>()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) {
        String userEmail = ((User) authResult.getPrincipal()).getUsername();
        UserDTO userDetails = userService.getUserDetailsByEmail(userEmail);

        String secretToken = environment.getProperty("token.secret");

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(setTokenExpirationTime())
                .signWith(SignatureAlgorithm.HS256, secretToken)
                .compact();

        response.addHeader("token", "Bearer " + token);
        response.addHeader("UserId", userDetails.getUserId());
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        String expirationTimeStr = environment.getProperty("token.expiration-time");
        if (expirationTimeStr == null) throw new MissingEnvironmentVariableException("Missing token.expiration-time");
        long expirationTime = Long.parseLong(expirationTimeStr);

        return new Date(currentTime + expirationTime);
    }
}
