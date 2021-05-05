package com.healthy.gym.user.security;

import com.healthy.gym.user.component.token.TokenManager;
import com.healthy.gym.user.configuration.EmbeddedRedisServer;
import com.healthy.gym.user.configuration.tests.TestCountry;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

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

@SpringBootTest
@AutoConfigureMockMvc
class RedisLogoutHandlerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenManager tokenManager;

    @Autowired
    @SuppressWarnings("Embedded redis  server is needed to conduct a tests.")
    private EmbeddedRedisServer embeddedRedisServer; // Do not remove this.

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

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldSendProperMessageWhenTokenHasAlreadyExpired(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI logout = new URI("/logout");

        Date fixedExpiredTokenTime = new Date(System.currentTimeMillis() - 1);
        String token = getToken(fixedExpiredTokenTime);

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
                                jsonPath("$.message").value(messages.get("user.logout.token.expired")),
                                jsonPath("$.errors").isMap(),
                                jsonPath("$.errors", aMapWithSize(0))
                        )
                );
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
        String rawToken = getRawToken(setTokenExpirationTime());
        return tokenManager.getTokenPrefix() + " " + rawToken;
    }

    private String getToken(Date expirationTime) {
        String rawToken = getRawToken(expirationTime);
        return tokenManager.getTokenPrefix() + " " + rawToken;
    }

    private String getRawToken(Date expirationTime) {
        SignatureAlgorithm signatureAlgorithm = tokenManager.getSignatureAlgorithm();
        String signingKey = tokenManager.getSigningKey();

        return Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setExpiration(expirationTime)
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    @TestConfiguration
    static class RedisTestConfiguration {
        private final Environment environment;

        @Autowired
        public RedisTestConfiguration(Environment environment) {
            this.environment = environment;
        }

        @Bean
        public LettuceConnectionFactory connectionFactory(RedisStandaloneConfiguration configuration) {
            configuration.setPort(getRedisTestPort());
            configuration.setPassword(getRedisPassword());

            return new LettuceConnectionFactory(configuration);
        }

        private int getRedisTestPort() {
            String port = environment.getRequiredProperty("spring.redis.test.port");
            return Integer.parseInt(port);
        }

        private String getRedisPassword() {
            return environment.getRequiredProperty("spring.redis.password");
        }
    }
}