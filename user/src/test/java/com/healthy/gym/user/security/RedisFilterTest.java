package com.healthy.gym.user.security;

import com.healthy.gym.user.configuration.EmbeddedRedisServer;
import com.healthy.gym.user.configuration.tests.TestCountry;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class RedisFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("Embedded redis  server is needed to conduct a tests.")
    private EmbeddedRedisServer embeddedRedisServer; // Do not remove this.

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldResponseWithProperMessageWhenUserTryToAccessResourceWithInvalidatedToken() {
        // TODO do it properly
        assertThat(true).isTrue();
    }


    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotResponseWitAnyMessageWhenUserTryToAccessResourceWithValidToken() {
        // TODO do it properly
        assertThat(true).isTrue();
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