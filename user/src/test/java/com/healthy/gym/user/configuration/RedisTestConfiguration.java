package com.healthy.gym.user.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@TestConfiguration
public class RedisTestConfiguration {

    private final Environment environment;
    private final RedisProperties redisProperties;

    @Autowired
    public RedisTestConfiguration(Environment environment) {
        this.environment = environment;
        this.redisProperties = new RedisProperties();
    }

    @Bean
    @Primary
    public LettuceConnectionFactory testConnectionFactory() {
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(getRedisTestPort());
        redisStandaloneConfiguration.setPassword(getRedisPassword());

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    private int getRedisTestPort() {
        String port = environment.getRequiredProperty("spring.redis.port");
        return Integer.parseInt(port);
    }

    private String getRedisPassword() {
        return environment.getRequiredProperty("spring.redis.password");
    }
}
