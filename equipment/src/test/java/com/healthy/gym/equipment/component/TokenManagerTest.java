package com.healthy.gym.equipment.component;

import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@TestPropertySource(
        inheritProperties = false,
        properties = {
                "token.secret=testSecretToken",
                "token.expiration-time=360001",
                "authorization.token.header.name=Auth",
                "authorization.token.header.prefix=Bear"
        }
)
@ActiveProfiles(value = "test")
class TokenManagerTest {

    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @Autowired
    private TokenManager tokenManager;

    @Test
    void shouldReturnProperSigningKey() {
        assertThat(tokenManager.getSigningKey()).isEqualTo("testSecretToken");
    }

    @Test
    void shouldReturnProperTokenPrefix() {
        assertThat(tokenManager.getTokenPrefix()).isEqualTo("Bear");
    }

    @Test
    void shouldReturnProperAuthorizationHeader() {
        assertThat(tokenManager.getHttpHeaderName()).isEqualTo("Auth");
    }

    @Test
    void shouldReturnProperExpirationTime() {
        assertThat(tokenManager.getExpirationTimeInMillis()).isEqualTo(360001);
    }

    @Test
    void shouldReturnProperSignatureAlgorithm() {
        assertThat(tokenManager.getSignatureAlgorithm()).isEqualTo(SignatureAlgorithm.HS256);
    }
}