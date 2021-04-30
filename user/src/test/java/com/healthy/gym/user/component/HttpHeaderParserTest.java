package com.healthy.gym.user.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class HttpHeaderParserTest {

    @Autowired
    private Environment environment;

    @Autowired
    private HttpHeaderParser httpHeaderParser;

    private HttpServletRequest request;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        authorizationHeader = "Authorization";
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderIsAbsent() {
        when(request.getHeader(authorizationHeader)).thenReturn(null);
        assertThat(httpHeaderParser.getAuthenticationToken(request)).isNull();
    }

    @Test
    void shouldReturnNullWhenAuthorizationHeaderValueDoesNotMatchTokenPrefix() {
        when(request.getHeader(authorizationHeader)).thenReturn("InvalidPrefix " + getToken());
        assertThat(httpHeaderParser.getAuthenticationToken(request)).isNull();
    }

    @Test
    void shouldReturnTokenWhenAuthorizationHeaderValueIsValid() {
        String token = getToken();
        String tokenPrefix = getTokenPrefix();
        when(request.getHeader(authorizationHeader)).thenReturn(tokenPrefix + " " + token);
        assertThat(httpHeaderParser.getAuthenticationToken(request)).isEqualTo(token);
    }

    private String getTokenPrefix() {
        return environment.getRequiredProperty("authorization.token.header.prefix");
    }

    private String getToken() {
        return Jwts
                .builder()
                .setSubject(getUserId())
                .setExpiration(getExpirationTime())
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    private String getUserId() {
        return UUID.randomUUID().toString();
    }

    private Date getExpirationTime() {
        long currentSystemTime = System.currentTimeMillis();
        long expirationTime = getExpirationTimeProperty();

        return new Date(currentSystemTime + expirationTime);
    }

    private long getExpirationTimeProperty() {
        String expirationTime = environment.getRequiredProperty("token.expiration-time");
        return Long.parseLong(expirationTime);
    }

    private String getSigningKey() {
        return environment.getRequiredProperty("token.secret");
    }
}