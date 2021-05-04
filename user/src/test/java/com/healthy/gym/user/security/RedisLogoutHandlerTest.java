package com.healthy.gym.user.security;

import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.configuration.RedisTestConfiguration;
import com.healthy.gym.user.configuration.tests.TestCountry;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import redis.embedded.RedisServer;

import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.user.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.user.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(RedisTestConfiguration.class)
@AutoConfigureMockMvc
class RedisLogoutHandlerTest {

    public static RedisServer redisServer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenManager tokenManager;

    @BeforeAll
    static void beforeAll() {
        int testRedisPort = 6380;
        String password = " thisP@sswordNeed2BeChange";

        redisServer = RedisServer.builder()
                .port(testRedisPort)
                .bind("127.0.0.1")
                .setting("requirepass " + password)
                .build();
        redisServer.start();
    }

    @AfterAll
    static void afterAll() {
        redisServer.stop();
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldFailLogoutUserWhenNoTokenProvided(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        RequestBuilder logoutRequest = get(logout).locale(testedLocale);

        mockMvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isUnauthorized(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                content().encoding("UTF-8"),
                                header().exists("Content-Language"),
                                header().string("Content-Language", testedLocale.getLanguage()),
                                jsonPath("$.success").value(false),
                                jsonPath("$.message").value(messages.get("user.logout.fail")),
                                jsonPath("$.errors").isMap(),
                                jsonPath("$.errors", aMapWithSize(1)),
                                jsonPath("$.errors", hasKey("token")),
                                jsonPath("$.errors.token").value(messages.get("user.logout.invalid.token"))
                        )
                );
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldFailLogoutUserWhenInvalidTokenProvided(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        RequestBuilder logoutRequest = get(logout)
                .header(tokenManager.getHttpHeaderName(), "invalidTestToken")
                .locale(testedLocale);

        mockMvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isUnauthorized(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                content().encoding("UTF-8"),
                                header().exists("Content-Language"),
                                header().string("Content-Language", testedLocale.getLanguage()),
                                jsonPath("$.success").value(false),
                                jsonPath("$.message").value(messages.get("user.logout.fail")),
                                jsonPath("$.errors").isMap(),
                                jsonPath("$.errors", aMapWithSize(1)),
                                jsonPath("$.errors", hasKey("token")),
                                jsonPath("$.errors.token").value(messages.get("user.logout.invalid.token"))
                        )
                );
    }

    @Disabled
    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSendProperMessageWhenTokenHasAlreadyExpired(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSuccessfullyLogoutUserWhenValidTokenProvided(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        String token = getToken();

        RequestBuilder logoutRequest = get(logout)
                .header(tokenManager.getHttpHeaderName(), token)
                .locale(testedLocale);

        mockMvc.perform(logoutRequest)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                content().encoding("UTF-8"),
                                header().exists("Content-Language"),
                                header().string("Content-Language", testedLocale.getLanguage()),
                                jsonPath("$.success").value(true),
                                jsonPath("$.message").value(messages.get("user.logout.success")),
                                jsonPath("$.errors").isMap(),
                                jsonPath("$.errors", aMapWithSize(0))
                        )
                );
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