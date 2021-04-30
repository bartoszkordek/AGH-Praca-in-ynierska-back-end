package com.healthy.gym.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.user.component.Translator;
import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.pojo.request.LogInUserRequest;
import com.healthy.gym.user.service.UserService;
import com.healthy.gym.user.shared.UserDTO;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Translator translator;
    private final TokenManager tokenManager;

    @Autowired
    public AuthenticationFilter(
            UserService userService,
            Translator translator,
            TokenManager tokenManager
    ) {
        this.userService = userService;
        this.translator = translator;
        this.tokenManager = tokenManager;
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
    ) throws IOException, ServletException {

        String userEmail = ((User) authResult.getPrincipal()).getUsername();
        UserDTO userDetails = userService.getUserDetailsByEmail(userEmail);

        String token = getTokenForUser(userDetails);

        response.addHeader("token", "Bearer " + token);
        response.addHeader("userId", userDetails.getUserId());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getTokenForUser(UserDTO userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }


    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        Map<String, String> body = Stream.of(new String[][]{
                {"timestamp", LocalDateTime.now().toString()},
                {"status", String.valueOf(response.getStatus())},
                {"error", "Unauthorized"},
                {"message", translator.toLocale("user.log-in.fail")},
                {"path", "/login"}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        ObjectMapper objectMapper = new ObjectMapper();
        String bodyAsString = objectMapper.writeValueAsString(body);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setLocale(LocaleContextHolder.getLocale());
        response.getWriter().println(bodyAsString);
    }
}
