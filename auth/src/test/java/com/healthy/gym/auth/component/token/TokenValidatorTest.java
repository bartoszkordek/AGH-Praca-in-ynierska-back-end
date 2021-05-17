package com.healthy.gym.auth.component.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TokenValidatorTest {
    private final String testToken = "testToken";
    private final String testPrefix = "Bearer";
    private final String testSigningKey = "testSigningKey";

    @Autowired
    private TokenValidator tokenValidator;

    @Nested
    class ValidArgumentProvided {
        private Date tokenIssueAt;
        private Date expiration;
        private String token;
        private String userId;
        private String prefixedToken;
        private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

        @BeforeEach
        void setUp() {
            long expirationTime = 300_000; // 5 minutes
            userId = UUID.randomUUID().toString();
            Instant instant = Instant.now();
            tokenIssueAt = Date.from(instant);

            token = Jwts.builder()
                    .setSubject(userId)
                    .setIssuedAt(tokenIssueAt)
                    .setExpiration(
                            Date.from(
                                    instant.plusMillis(expirationTime)
                            )
                    )
                    .signWith(SignatureAlgorithm.HS256, testSigningKey)
                    .compact();

            expiration = Jwts
                    .parser()
                    .setSigningKey(testSigningKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            prefixedToken = testPrefix + " " + token;
            usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
        }

        @Test
        void shouldReturnTokenExpirationTime() {
            assertThat(tokenValidator.getTokenExpirationTime(token, testSigningKey))
                    .isEqualTo(expiration);
        }

        @Test
        void shouldReturnTokenExpirationTimeWhenTokenHasPrefix() {
            assertThat(tokenValidator.getTokenExpirationTime(prefixedToken, testPrefix, testSigningKey))
                    .isEqualTo(expiration);
        }

        @Test
        void shouldReturnUsernamePasswordAuthToken() {
            assertThat(tokenValidator.getAuthentication(token, testSigningKey))
                    .isEqualTo(usernamePasswordAuthenticationToken);
        }

        @Test
        void shouldReturnUsernamePasswordAuthTokenWhenTokenHasPrefix() {
            assertThat(tokenValidator.getAuthentication(prefixedToken, testPrefix, testSigningKey))
                    .isEqualTo(usernamePasswordAuthenticationToken);
        }

        @Test
        void shouldPurifyToken() {
            assertThat(tokenValidator.purifyToken(prefixedToken, testPrefix))
                    .isEqualTo(token);
        }

        @Test
        void shouldPurifyTokenWhenPrefixTokenIsNull() {
            String testTokenToTrim = " " + token + " ";
            assertThat(tokenValidator.purifyToken(testTokenToTrim, null))
                    .isEqualTo(token);
        }

        @Nested
        class shouldThrowAuthenticationServiceExceptionWithProperMessage {

            private String invalidTestSigningKey;
            private String invalidTestToken;
            private String invalidPrefixedTestToken;
            private String invalidTokenMessage = "Invalid token.";

            @BeforeEach
            void setUp() {
                invalidTestSigningKey = "testInvalidSigningKey";
                invalidTestToken = Jwts.builder()
                        .setSubject(userId)
                        .setIssuedAt(tokenIssueAt)
                        .setExpiration(expiration)
                        .signWith(SignatureAlgorithm.HS256, invalidTestSigningKey)
                        .compact();
                invalidPrefixedTestToken = testPrefix + " " + invalidTestToken;
            }

            @Nested
            class whenGettingTokenExpirationTime {
                @Test
                void withInvalidSigningKey() {
                    assertThatThrownBy(
                            () -> tokenValidator.getTokenExpirationTime(token, invalidTestSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidSigningKeyAndValidPrefixedToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getTokenExpirationTime(token, testPrefix, invalidTestSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidTestToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getTokenExpirationTime(invalidTestToken, testSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidPrefixedTestToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getTokenExpirationTime(invalidPrefixedTestToken, testPrefix, testSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }
            }

            @Nested
            class whenGettingAuthentication {
                @Test
                void withInvalidSigningKey() {
                    assertThatThrownBy(
                            () -> tokenValidator.getAuthentication(token, invalidTestSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidSigningKeyAndValidPrefixedToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getAuthentication(token, testPrefix, invalidTestSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidTestToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getAuthentication(invalidTestToken, testSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }

                @Test
                void withInvalidPrefixedTestToken() {
                    assertThatThrownBy(
                            () -> tokenValidator.getAuthentication(invalidPrefixedTestToken, testPrefix, testSigningKey)
                    ).isInstanceOf(AuthenticationServiceException.class).hasMessage(invalidTokenMessage);
                }
            }
        }
    }

    @Nested
    class InvalidArgumentProvided {

        @Nested
        class TokenIsNullAndShouldThrowIllegalArgumentExceptionWithProperMessage {
            private final String exceptionMessage = "Token can not be null.";

            @Test
            void whenGettingAuthentication() {
                assertThatThrownBy(
                        () -> tokenValidator.getAuthentication(null, testSigningKey)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingAuthenticationWithTokenPrefix() {
                assertThatThrownBy(
                        () -> tokenValidator.getAuthentication(null, testPrefix, testSigningKey)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingTokenExpirationTime() {
                assertThatThrownBy(
                        () -> tokenValidator.getTokenExpirationTime(null, testSigningKey)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingTokenExpirationTimeWithTokenPrefix() {
                assertThatThrownBy(
                        () -> tokenValidator.getTokenExpirationTime(null, testPrefix, testSigningKey)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenPurifyingToken() {
                assertThatThrownBy(
                        () -> tokenValidator.purifyToken(null, testPrefix)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenPurifyingTokenWithNullPrefixToken() {
                assertThatThrownBy(
                        () -> tokenValidator.purifyToken(null, null)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }
        }

        @Nested
        class SigningKeyIsNullAndShouldThrowIllegalArgumentExceptionWithProperMessage {
            private final String exceptionMessage = "Signing key can not be null.";

            @Test
            void whenGettingAuthentication() {
                assertThatThrownBy(
                        () -> tokenValidator.getAuthentication(testToken, null)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingAuthenticationWithTokenPrefix() {
                assertThatThrownBy(
                        () -> tokenValidator.getAuthentication(testToken, testPrefix, null)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingTokenExpirationTime() {
                assertThatThrownBy(
                        () -> tokenValidator.getTokenExpirationTime(testToken, null)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }

            @Test
            void whenGettingTokenExpirationTimeWithTokenPrefix() {
                assertThatThrownBy(
                        () -> tokenValidator.getTokenExpirationTime(testToken, testPrefix, null)
                ).isInstanceOf(IllegalArgumentException.class).hasMessage(exceptionMessage);
            }
        }
    }
}