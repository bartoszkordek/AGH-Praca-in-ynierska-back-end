package com.healthy.gym.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {
    private final Environment environment;

    @Autowired
    public RedisConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    public LettuceConnectionFactory connectionFactory(RedisStandaloneConfiguration configuration) {
        configuration.setPassword(getRedisPassword());
        return new LettuceConnectionFactory(configuration);
    }

    private String getRedisPassword() {
        return environment.getRequiredProperty("spring.redis.password");
    }
}
