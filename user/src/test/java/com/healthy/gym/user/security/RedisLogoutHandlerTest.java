package com.healthy.gym.user.security;

import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.configuration.tests.TestCountry;
import com.healthy.gym.user.pojo.response.LogoutResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.user.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.user.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisLogoutHandlerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TokenManager tokenManager;

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldFailLogoutUserWhenNoTokenProvided(TestCountry country) throws URISyntaxException {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        RequestEntity<Void> logoutRequest = RequestEntity
                .get(logout)
                .header("Accept-Language", testedLocale.toString())
                .build();

        ResponseEntity<LogoutResponse> logoutResponse = restTemplate.exchange(logoutRequest, LogoutResponse.class);

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(logoutResponse.getBody().isSuccess()).isFalse();
        assertThat(logoutResponse.getBody().getMessage()).isEqualTo(messages.get("user.logout.fail"));
        assertThat(logoutResponse.getBody().getErrors()).hasSize(1);
        assertThat(logoutResponse.getBody().getErrors())
                .containsEntry("token", messages.get("user.logout.invalid.token"));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldFailLogoutUserWhenInvalidTokenProvided(TestCountry country) throws URISyntaxException {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        RequestEntity<Void> logoutRequest = RequestEntity
                .get(logout)
                .header("Accept-Language", testedLocale.toString())
                .build();

        ResponseEntity<LogoutResponse> logoutResponse = restTemplate.exchange(logoutRequest, LogoutResponse.class);

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(logoutResponse.getBody().isSuccess()).isFalse();
        assertThat(logoutResponse.getBody().getMessage()).isEqualTo(messages.get("user.logout.fail"));
        assertThat(logoutResponse.getBody().getErrors()).hasSize(1);
        assertThat(logoutResponse.getBody().getErrors())
                .containsEntry("token", messages.get("user.logout.invalid.token"));
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSendProperMessageWhenTokenHasAlreadyExpired(TestCountry country) throws URISyntaxException {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSuccessfullyLogoutUserWhenValidTokenProvided(TestCountry country) throws URISyntaxException {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        RequestEntity<Void> logoutRequest = RequestEntity
                .get(logout)
                .header("Accept-Language", testedLocale.toString())
                .header(tokenManager.getHttpHeaderName(), getToken())
                .build();

        ResponseEntity<LogoutResponse> logoutResponse = restTemplate.exchange(logoutRequest, LogoutResponse.class);

        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(logoutResponse.getBody().isSuccess()).isTrue();
        assertThat(logoutResponse.getBody().getMessage()).isEqualTo(messages.get("user.logout.success"));
        assertThat(logoutResponse.getBody().getErrors()).isEmpty();

    }

    private String getToken() {
        String signingKey = tokenManager.getSigningKey();
        SignatureAlgorithm signatureAlgorithm = tokenManager.getSignatureAlgorithm();

        String rawToken = Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setExpiration(setTokenExpirationTime())
                .signWith(signatureAlgorithm, signingKey)
                .compact();

        return tokenManager.getTokenPrefix() + " " + rawToken;
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }
}