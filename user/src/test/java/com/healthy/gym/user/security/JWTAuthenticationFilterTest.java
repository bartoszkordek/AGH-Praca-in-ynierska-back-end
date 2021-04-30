package com.healthy.gym.user.security;

import com.healthy.gym.user.component.HttpHeaderParser;
import com.healthy.gym.user.component.TokenValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {

    private AuthenticationManager authenticationManager;
    private Environment environment;
    private TokenValidator tokenValidator;
    private HttpHeaderParser httpHeaderParser;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private JWTAuthenticationFilter jwtFilter;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        environment = mock(Environment.class);
        tokenValidator = mock(TokenValidator.class);
        httpHeaderParser = mock(HttpHeaderParser.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);

        jwtFilter = new JWTAuthenticationFilter(
                authenticationManager,
                environment,
                tokenValidator,
                httpHeaderParser
        );
    }

    @Nested
    class shouldBeAuthenticated {

        @Test
        void whenValidTokenProvided() throws IOException, ServletException {
            when(httpHeaderParser.getAuthenticationToken(any())).thenReturn("testToken");
            when(tokenValidator.getAuthentication(any(), any(), any())).thenReturn(
                    new UsernamePasswordAuthenticationToken("testID", null, new ArrayList<>())
            );

            jwtFilter.doFilterInternal(request, response, chain);

            verify(tokenValidator, times(1)).getAuthentication(any(), any(), any());
            verify(chain, times(1)).doFilter(any(), any());

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        }
    }

    @Nested
    class shouldNotBeAuthenticated {

        @Test
        void whenNoTokenProvided() throws IOException, ServletException {
            when(httpHeaderParser.getAuthenticationToken(any())).thenReturn(null);

            jwtFilter.doFilterInternal(request, response, chain);

            verify(tokenValidator, times(0)).getAuthentication(any(), any(), any());
            verify(chain, times(1)).doFilter(any(), any());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void whenInvalidTokePrefixOrSigningKeyProvided() throws IOException, ServletException {
            when(httpHeaderParser.getAuthenticationToken(any())).thenReturn("testToken");
            TokenValidator spyTokenValidator = spy(tokenValidator);

            doThrow(IllegalArgumentException.class).when(spyTokenValidator).getAuthentication(any(), any(), any());

            jwtFilter.doFilterInternal(request, response, chain);

            verify(tokenValidator, times(1)).getAuthentication(any(), any(), any());
            verify(chain, times(1)).doFilter(any(), any());

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        void whenInvalidTokenProvided() throws IOException, ServletException {
            when(httpHeaderParser.getAuthenticationToken(any())).thenReturn("testToken");
            TokenValidator spyTokenValidator = spy(tokenValidator);

            doThrow(AuthenticationServiceException.class).when(spyTokenValidator).getAuthentication(any(), any(), any());

            jwtFilter.doFilterInternal(request, response, chain);

            verify(tokenValidator, times(1)).getAuthentication(any(), any(), any());
            verify(chain, times(1)).doFilter(any(), any());

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
}